package com.commercetools.bulkpricer;

import com.commercetools.bulkpricer.apimodel.CtpExtensionRequestBody;
import com.commercetools.bulkpricer.apimodel.CtpExtensionUpdateRequestedResponse;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.updateactions.SetLineItemPrice;
import io.sphere.sdk.products.Price;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.util.HashMap;
import java.util.Map;

public class BulkPricer extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(BulkPricer.class);

  private Map<String, JsonObject> products = new HashMap<>();

  @Override
  public void start() {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.get("/prices/:groupKey/:sku")
      .produces("application/json")
      .handler(this::handleGetPrice);

    router.put("/prices/:groupKey/:sku")
      .consumes("application/json")
      .handler(this::handlePutPrice);

    router.post("/prices/for-cart/extend-with-external-prices")
      .consumes("application/json")
      .produces("application/json")
      .handler(this::handleExtendCartWithExternalPrices);

    router.post("/prices/import-from-url")
      .consumes("application/json")
      .produces("application/json")
      .handler(this::handleImportJobSubmission);

    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }

  private void handleGetPrice(RoutingContext routingContext) {
    MonetaryAmount amount = lookUpPrice(routingContext.pathParam("groupKey"), routingContext.pathParam("sku"));
    routingContext.response()
      .setStatusCode(200)
      .end(JsonObject.mapFrom(Price.of(amount)).toBuffer());
  }

  private void handlePutPrice(RoutingContext routingContext) {

  }

  private void handleExtendCartWithExternalPrices(RoutingContext routingContext) {
    // 200 or 201 for successful responses, 400 for validation failures
    try {
      JsonObject bodyJson = routingContext.getBody().toJsonObject();
      logger.info(bodyJson.toString());
      CtpExtensionRequestBody extensionRequest = bodyJson.mapTo(CtpExtensionRequestBody.class);
      Cart cart = extensionRequest.getResource().getObj();

      // TODO damn the customer Group is just a reference. -> this will never match without fetching it separately and caching it
      // String groupKey = cart.getCustomerGroup().getObj().getKey();
      // interim approach to test:

      CtpExtensionUpdateRequestedResponse extensionResponse = new CtpExtensionUpdateRequestedResponse();

      cart.getLineItems().forEach((LineItem lineItem) -> {
        String sku = lineItem.getVariant().getSku();
        String groupKey = cart.getCustomerGroup().getId();
        String customerId = cart.getCustomerId();
        if (groupKey != null) {
          MonetaryAmount groupPrice = lookUpPrice(groupKey, sku);
          if (groupPrice != null) {
            extensionResponse.appendUpdateAction(SetLineItemPrice.of(lineItem, groupPrice));
          }
        }
        if (customerId != null) {
          MonetaryAmount customerPrice = lookUpPrice(customerId, sku);
            if (customerPrice != null) {
              extensionResponse.appendUpdateAction(SetLineItemPrice.of(lineItem, customerPrice));
            }
          }

        routingContext.response()
          .setStatusCode(200)
          .end(JsonObject.mapFrom(extensionResponse).toBuffer());
      });

    } catch (DecodeException e) {
      routingContext.response().setStatusCode(400).setStatusMessage("HTTP body must be valid JSON");
    }

  }

  private MonetaryAmount lookUpPrice(String groupKey, String sku) {
    LocalMap<String, ShareablePriceList> sharedPrices = vertx.sharedData().getLocalMap("prices");
    if (sharedPrices == null) {
      return null;
    }
    ShareablePriceList groupPriceList = sharedPrices.get(groupKey);
    if (groupPriceList == null) {
      return null;
    }
    int price = groupPriceList.getPrices().get(sku);
    return Money.ofMinor(groupPriceList.getCurrency(), price);
  }

  private void handleImportJobSubmission(RoutingContext routingContext) {
    vertx.eventBus().send("bulkpricer.loadrequests", routingContext.getBodyAsString(), response -> {
      if (response.succeeded()) {
        routingContext.response().setStatusCode(202).end(
          response.result().body().toString()
        );
      } else {
        logger.error("Can't send message to hello service", response.cause());
        routingContext.response().setStatusCode(500).end(
          response.cause().getMessage()
        );
      }
    });

  }

}


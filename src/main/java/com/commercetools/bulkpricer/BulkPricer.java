package com.commercetools.bulkpricer;

import com.commercetools.bulkpricer.com.commercetools.bulkpricer.apiModel.CtpExtensionRequestBody;
import com.commercetools.bulkpricer.com.commercetools.bulkpricer.apiModel.CtpExtensionUpdateRequestedResponse;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.updateactions.SetLineItemPrice;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class BulkPricer extends AbstractVerticle {

  private Map<String, JsonObject> products = new HashMap<>();

  @Override
  public void start() {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.post("/prices/for-cart/extend-with-external-prices")
      .consumes("application/json")
      .produces("application/json")
      .handler(this::handleExtendCartWithExternalPrices);

    router.post("prices/imports/:groupId")
      .consumes("application/json")
      .produces("application/json")
      .handler(this::handleImportJobSubmission);

    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }

  private void handleExtendCartWithExternalPrices(RoutingContext routingContext) {
    // 200 or 201 for successful responses, 400 for validation failures
    JsonObject bodyJson = routingContext.getBody().toJsonObject();
    CtpExtensionRequestBody extensionRequest = bodyJson.mapTo(CtpExtensionRequestBody.class);
    Cart cart = extensionRequest.resource.getObj();
    // TODO damn the customer Group is just a reference. -> this will never match without fetching it separately and caching it
    // String groupKey = cart.getCustomerGroup().getObj().getKey();
    // interim approach to test:

    CtpExtensionUpdateRequestedResponse extensionResponse = new CtpExtensionUpdateRequestedResponse();

    cart.getLineItems().forEach((LineItem lineItem) -> {
      String sku = lineItem.getVariant().getSku();
      String groupKey = cart.getCustomerGroup().getId();
      String customerId = cart.getCustomerId();
      try {
        int skuInt = NumberFormat.getIntegerInstance().parse(sku).intValue();
        if (groupKey != null){
          MonetaryAmount groupPrice = lookUpPrice(groupKey, skuInt);
          if (groupPrice != null){ extensionResponse.appendUpdateAction(SetLineItemPrice.of(lineItem,groupPrice));}
        }
        if (customerId != null){
          MonetaryAmount customerPrice = lookUpPrice(customerId, skuInt);
          if (customerPrice != null){ extensionResponse.appendUpdateAction(SetLineItemPrice.of(lineItem,customerPrice)); }
        }
      } catch (ParseException e) {
        //
        routingContext.response().setStatusCode(400).setStatusMessage("External Pricing is only supported for numeric integer SKUs with this service");
      }

      routingContext.response()
        .setStatusCode(200)
        .end(JsonObject.mapFrom(extensionResponse).toBuffer());
    });
  }

  private MonetaryAmount lookUpPrice(String groupKey, int sku){
    LocalMap<String, ShareablePriceList> sharedPrices = vertx.sharedData().getLocalMap("prices");
    if(sharedPrices == null){return null;}
    ShareablePriceList groupPriceList = sharedPrices.get(groupKey);
    if(groupPriceList == null){return null;}
    int price = groupPriceList.getPrices().get(sku);
    return Money.ofMinor(groupPriceList.getCurrency(), price);
  }

  private void handleImportJobSubmission(RoutingContext routingContext) {
    vertx.eventBus().publish("bulkpricer.loadrequests", routingContext.getBodyAsString());
    // TODO pass back the message response and don't set it here.
    routingContext.response().setStatusCode(202).end();
  }

}


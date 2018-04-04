package com.commercetools.bulkpricer;

import com.commercetools.bulkpricer.apimodel.CtpExtensionRequestBody;
import com.commercetools.bulkpricer.apimodel.CtpExtensionUpdateRequestedResponse;
import com.commercetools.bulkpricer.apimodel.CtpMoneyRepresentation;
import com.commercetools.bulkpricer.helpers.CorrelationId;
import com.commercetools.bulkpricer.helpers.CtpMetadataStorage;
import com.commercetools.bulkpricer.helpers.JsonUtils;
import com.commercetools.bulkpricer.messages.JsonBusMessage;
import com.commercetools.bulkpricer.messages.JsonBusMessageCodec;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.updateactions.SetLineItemPrice;
import io.sphere.sdk.customobjects.CustomObject;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BulkPriceHttpApi extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(BulkPriceHttpApi.class);

  @Override
  public void start() {

    vertx.eventBus().registerDefaultCodec(JsonBusMessage.class, new JsonBusMessageCodec());

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.get("/prices/groups")
      .produces("application/json")
      .handler(this::handleGetGroups);

    // e.g.  /prices/groups/mygroup?1235,5466757,A35548700657,78478Z5
    router.get("/prices/groups/:groupKey")
      .handler(this::handleQueryPrices);

    router.delete("/prices/groups/:groupKey")
      .handler(this::handleDeletePriceGroup);

    router.get("/prices/groups/:groupKey/:sku")
      .produces("application/json")
      .handler(this::handleGetPrice);

    router.post("/prices/for-cart/extend-with-external-prices")
      .consumes("application/json")
      .handler(this::handleExtendCartWithExternalPrices);

    router.post("/prices/load-from-url")
      .consumes("application/json")
      .handler(this::handleLoadJobSubmission);

    // Idea: an endpoint that accepts AWS SNS notifications of S3 bucket changes about new or updated fieles ( s3:ObjectCreated:* )
    // /prices/load-from-url/s3-bucket-message-sink
    // https://docs.aws.amazon.com/sns/latest/dg/SendMessageToHttp.html
    // https://docs.aws.amazon.com/AmazonS3/latest/dev/notification-content-structure.html

    vertx.createHttpServer().requestHandler(router::accept).listen(getPort());
  }

  private void handleGetGroups(RoutingContext routingContext) {
    LocalMap<String, ShareablePriceList> sharedPrices = vertx.sharedData().getLocalMap("prices");
    HashMap<String, Object> response = new HashMap<>();
    if (sharedPrices != null) {
      sharedPrices.forEach(response::put);
    }
    routingContext.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(200)
      .end(JsonObject.mapFrom(response).encodePrettily());
  }

  private void handleDeletePriceGroup(RoutingContext routingContext) {
    String groupKey = routingContext.pathParam("groupKey");
    if (groupKey != null) {
      LocalMap<String, ShareablePriceList> sharedPrices = vertx.sharedData().getLocalMap("prices");
      if(sharedPrices.containsKey(groupKey)){
        sharedPrices.remove(groupKey);
      }
      vertx.<CustomObject<ShareablePriceList>>executeBlocking(ebFuture ->
          ebFuture.complete(CtpMetadataStorage.deletePriceListMetadata(groupKey)
        ), res -> vertx.eventBus().publish(Topics.deleteresults, new JsonObject()
          .put("statusCode", 200)
          .put("statusMessage", "deleted price list and metadata for group:" + groupKey))
      );
      routingContext.response()
        .setStatusCode(202)
        .setStatusMessage("price group deletion request accepted and deleted on current node").end();
    }else{
      routingContext.response()
        .setStatusCode(404)
        .setStatusMessage("no price list with given groupKey found");
    }
  }

  private void handleGetPrice(RoutingContext routingContext) {
    MonetaryAmount amount = lookUpPrice(routingContext.pathParam("groupKey"), routingContext.pathParam("sku"));
    if (amount != null) {
      CtpMoneyRepresentation money = new CtpMoneyRepresentation(amount);
      routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200).end(JsonObject.mapFrom(money).encodePrettily());
    } else {
      routingContext.response()
        .setStatusCode(404)
        .setStatusMessage("Could not find a price for given groupKey and SKU").end();
    }
  }

  private void handleQueryPrices(RoutingContext routingContext){
    String groupKey = routingContext.request().getParam("groupKey");
    ArrayList<String> skus = new ArrayList<>(Arrays.asList(routingContext.request().getParam("skus").split(",")));
    Map<String, CtpMoneyRepresentation> responseBody = new HashMap<>();
    skus.forEach(sku -> {
      MonetaryAmount price = lookUpPrice(groupKey,sku);
      if(price != null){
        responseBody.put(sku, new CtpMoneyRepresentation(price));
      }
      routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200).end(JsonUtils.toJsonString(responseBody));
    });

  }

  private void handleExtendCartWithExternalPrices(RoutingContext routingContext) {

    try {
      JsonObject bodyJson = routingContext.getBody().toJsonObject();
      logger.info(bodyJson.toString());
      CtpExtensionRequestBody extensionRequest = JsonUtils.readObject(bodyJson.toString(), CtpExtensionRequestBody.class);
      Cart cart = extensionRequest.getResource().getObj();

      CtpExtensionUpdateRequestedResponse extensionResponse = new CtpExtensionUpdateRequestedResponse();

      cart.getLineItems().forEach((LineItem lineItem) -> {
        String sku = lineItem.getVariant().getSku();
        MonetaryAmount customerPrice = null;
        if (cart.getCustomerId() != null) {
          customerPrice = lookUpPrice(cart.getCustomerId(), sku);
          if (customerPrice != null) {
            extensionResponse.appendUpdateAction(SetLineItemPrice.of(lineItem, customerPrice));
          }
        }
        // TODO damn the customer Group is just a reference. -> this will never match without fetching it separately and caching it
        // String groupKey = cart.getCustomerGroup().getObj().getKey();
        // interim approach to test: use the raw ID
        if (customerPrice == null && cart.getCustomerGroup() != null) {
          MonetaryAmount groupPrice = lookUpPrice(cart.getCustomerGroup().getId(), sku);
          if (groupPrice != null) {
            extensionResponse.appendUpdateAction(SetLineItemPrice.of(lineItem, groupPrice));
          }
        }

        routingContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(200)
          .end(JsonUtils.toJsonString(extensionResponse));
      });

    } catch (DecodeException e) {
      routingContext.response()
        .setStatusCode(400)
        .setStatusMessage("HTTP body must be valid JSON - " + e.getMessage()).end();
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

  private void handleLoadJobSubmission(RoutingContext routingContext) {
    vertx.eventBus().send(Topics.loadrequests, routingContext.getBodyAsString(), response -> {
      if (response.succeeded()) {
        JsonObject responseMessage = new JsonObject((response.result().body().toString()));
        routingContext.response()
          .setStatusCode(responseMessage.getInteger("statusCode"))
          .putHeader(CorrelationId.headerName, response.result().headers().get(CorrelationId.headerName))
          .setStatusMessage(responseMessage.getString("statusMessage")).end();
      } else {
        logger.error("couldn't submit load job to loader via event bus", response.cause());
        routingContext.response().setStatusCode(500)
          .putHeader(CorrelationId.headerName, response.result().headers().get(CorrelationId.headerName))
          .end(response.cause().getMessage());
      }
    });
  }

  private int getPort() {
    int port = 8080;
    try {
      // heroku convention:
      String portEnv = System.getenv("PORT");
      if (portEnv != null) {
        int envPort = NumberFormat.getIntegerInstance().parse(portEnv).intValue();
        if (envPort > 0) port = envPort;
      }
    } catch (ParseException e) {
    }
    return port;
  }

}


package com.commercetools.bulkpricer;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class PriceFileServerTestVerticle extends AbstractVerticle {
  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.get("/random-prices/:amount")
      .handler(request -> {
        request.response()
          .setStatusCode(200)
          .end(ExampleData.getRandomPriceLines(request.pathParam("amount")));
      });

    vertx.createHttpServer().requestHandler(router::accept).listen(8081);
  }
}

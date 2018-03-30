package com.commercetools.bulkpricer;

import com.commercetools.bulkpricer.helpers.JsonUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(){

    JsonUtils.configureVertxMappers(Json.mapper, Json.prettyMapper);

    final List<AbstractVerticle> verticles = Arrays.asList(
      new BulkPriceProvider(),
      new BulkPriceHttpApi(),
      new PriceFileServerTestVerticle());

    verticles.forEach(verticle -> vertx.deployVerticle(verticle, deployResponse -> {
      if (deployResponse.failed()) {
        logger.error("Unable to deploy verticle " + verticle.getClass().getSimpleName(),
          deployResponse.cause());
      } else {
        logger.info(verticle.getClass().getSimpleName() + " deployed");
      }
    }));
  }

  public static void main(final String... args) {
    final Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}

package com.commercetools.bulkpricer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Random;

public class PriceFileServerTestVerticle extends AbstractVerticle {
  @Override
  public void start() {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.get("/random-prices/:amount")
      .handler(request -> {
        request.response()
          .setStatusCode(200)
          .end(getRandomPriceLines(request.pathParam("amount")));
      });

    vertx.createHttpServer().requestHandler(router::accept).listen(8081);
  }

  private Buffer getRandomPriceLines(int amount) {
    Random rnd = new Random();
    Buffer buf = Buffer.buffer(amount * 20);
    for (int i = 0; i < amount; i++) {
      buf.appendString(new Integer(rnd.nextInt() & Integer.MAX_VALUE).toString());
      buf.appendString(",");
      buf.appendString(new BigDecimal(rnd.nextInt() & Integer.MAX_VALUE).divide(new BigDecimal(100)).toString());
      buf.appendString("\n");
    }
    return buf;
  }

  private Buffer getRandomPriceLines(String amount){
    try {
      int a = NumberFormat.getIntegerInstance().parse(amount).intValue();
      return getRandomPriceLines(a);
    } catch (ParseException e) {
      return getRandomPriceLines(100);
    }
  }

}

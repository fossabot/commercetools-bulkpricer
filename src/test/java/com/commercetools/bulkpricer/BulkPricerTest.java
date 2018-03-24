package com.commercetools.bulkpricer;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.money.Monetary;
import java.io.IOException;
import java.text.ParseException;

@RunWith(VertxUnitRunner.class)
public class BulkPricerTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext tc) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(MainVerticle.class.getName(), tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  @Ignore
  public void testCartExtensionRoundtrip(TestContext tc) {
    Async async = tc.async();
    WebClient httpClient = WebClient.create(vertx);
    httpClient.post(8080, "localhost", "/prices/for-cart/extend-with-external-prices")
      .putHeader("content-type", "application/json")
      .sendJson(ExampleData.getTestExtensionRequestBody(), asyncResult -> {
        if (asyncResult.succeeded()) {
          HttpResponse<Buffer> response = asyncResult.result();
          tc.assertEquals(response.statusCode(), 200);
          // TODO actually assert the response body
        } else {
          tc.fail("calling extension api failed totally");
        }
        async.complete();
      });
  }

  @Test
  public void testLineParser(TestContext tc) throws ParseException {
    BulkPriceLoader bpl = new BulkPriceLoader();
    tc.assertEquals(
      PrimitiveTuples.pair(123456, 9998)
      , bpl.parseLine("123456,99.98", Monetary.getCurrency("EUR"))
    );
    tc.assertEquals(
      PrimitiveTuples.pair(123456, 9998)
      , bpl.parseLine("123456,99.980", Monetary.getCurrency("EUR"))
    );
    tc.assertEquals(
      PrimitiveTuples.pair(123456, 9998)
      , bpl.parseLine("123456,99.983", Monetary.getCurrency("EUR"))
    );
  }

  @Test
  public void testRemotePriceFileLoader(TestContext tc) throws IOException, ParseException {
    BulkPriceLoader bpl = new BulkPriceLoader();
    // vertx.deployVerticle(new PriceFileServerTestVerticle());
    IntIntHashMap randomPrices = bpl.readRemotePrices("http://localhost:8081/random-prices/1000", Monetary.getCurrency("EUR"));
    tc.assertEquals(1000, randomPrices.size());
  }

}

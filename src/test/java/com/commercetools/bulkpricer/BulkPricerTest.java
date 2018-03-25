package com.commercetools.bulkpricer;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
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

  private final Logger logger = LoggerFactory.getLogger(BulkPricer.class);

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
      .sendBuffer(Buffer.buffer(ExampleData.getCtpExtensionRequestBodyAsString()), asyncResult -> {
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
      PrimitiveTuples.pair("123456", 9998)
      , bpl.parseLine("123456,99.98", Monetary.getCurrency("EUR"))
    );
    tc.assertEquals(
      PrimitiveTuples.pair("123456", 9998)
      , bpl.parseLine("123456,99.980", Monetary.getCurrency("EUR"))
    );
    tc.assertEquals(
      PrimitiveTuples.pair("123456", 9998)
      , bpl.parseLine("123456,99.983", Monetary.getCurrency("EUR"))
    );
  }

  @Test
  public void testRemotePriceFileLoader(TestContext tc) throws IOException, ParseException {
    BulkPriceLoader bpl = new BulkPriceLoader();
    // IntIntHashMap randomPrices = bpl.readRemotePrices("http://localhost:8081/random-prices/1000", Monetary.getCurrency("EUR"));
    ShareablePriceList randomPrices = bpl.readRemotePrices("https://github.com/nkuehn/commercetools-bulkpricer/raw/master/src/test/resources/999999-prices.csv", Monetary.getCurrency("EUR"), "test-group");
    tc.assertEquals(999744, randomPrices.getPrices().size());
    tc.assertEquals((999999 - 999744), randomPrices.getDuplicateSkuCount());
  }

  @Test
  public void testPriceLoadByEvent(TestContext tc){
    vertx.eventBus().send(
      "bulkpricer.loadrequests",
      ExampleData.getPriceLoadRequestAsString("https://github.com/nkuehn/commercetools-bulkpricer/raw/master/src/test/resources/999999-prices.csv"),
      response -> {
        tc.assertTrue(response.succeeded());
        JsonObject respBody = new JsonObject(response.result().body().toString());
        tc.assertEquals(202, respBody.getInteger("statusCode"));
      });
    Async tcAsync = tc.async();
    vertx.eventBus().consumer("bulkpricer.loadresults", message -> {
      JsonObject params = new JsonObject(message.body().toString());
      tc.assertEquals(201, params.getInteger("statusCode"));
      tcAsync.complete();
    });
  }

  @Test
  @Ignore
  public void testPriceLoadByApi(TestContext tc){
    Async async = tc.async();
    WebClient httpClient = WebClient.create(vertx);
    httpClient.post(8080, "localhost", "/prices/load-from-url")
      .putHeader("content-type", "application/json")
      .sendBuffer(Buffer.buffer(ExampleData.getPriceLoadRequestAsString("https://github.com/nkuehn/commercetools-bulkpricer/raw/master/src/test/resources/999999-prices.csv")), asyncResult -> {
        if (asyncResult.succeeded()) {
          HttpResponse<Buffer> response = asyncResult.result();
          tc.assertEquals(200,response.statusCode());
          // TODO actually assert the response body
        } else {
          tc.fail("calling submit price load api failed totally");
        }
        async.complete();
      });
  }

}

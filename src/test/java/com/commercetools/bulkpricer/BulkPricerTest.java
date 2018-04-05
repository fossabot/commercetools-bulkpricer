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

  private final Logger logger = LoggerFactory.getLogger(BulkPriceHttpApi.class);

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
          logger.info(response.bodyAsString());
          JsonObject responseObj = new JsonObject(response.bodyAsString());
          // TODO FIXME how to make sure that test price data is already loaded?
          tc.assertEquals(1, responseObj.getJsonArray("actions").size());
          // should:  line item 5dac682a-257a-4ada-8062-cdcd756a294a  should get a price of 2483688.56 (the first in the example data)
          // {"actions":[{"action":"setLineItemPrice","lineItemId":"5dac682a-257a-4ada-8062-cdcd756a294a","externalPrice":{"centAmount":248368856,"currencyCode":"EUR"}}]}
          tc.assertEquals("5dac682a-257a-4ada-8062-cdcd756a294a",responseObj.getJsonArray("actions").getJsonObject(0).getString("lineItemId"));
          tc.assertEquals(248368856,responseObj.getJsonArray("actions").getJsonObject(0).getJsonObject("externalPrice").getInteger("centAmount"));
        } else {
          tc.fail("calling extension api failed totally");
        }
        async.complete();
      });
  }

  @Test
  public void testLineParser(TestContext tc) throws ParseException {
    BulkPriceProvider bpl = new BulkPriceProvider();
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
    BulkPriceProvider bpl = new BulkPriceProvider();
    // IntIntHashMap randomPrices = bpl.readRemotePrices("http://localhost:8081/random-prices/1000", Monetary.getCurrency("EUR"));
    ShareablePriceList randomPrices = bpl.readRemotePrices(ExampleData.millionPricesFileUrl, Monetary.getCurrency("EUR"), "test-group");
    tc.assertEquals(999744, randomPrices.getPrices().size());
    tc.assertEquals((999999 - 999744), randomPrices.getDuplicateSkuCount());
  }

  @Test
  public void testPriceLoadByEvent(TestContext tc){
    vertx.eventBus().send(
      "bulkpricer.loadrequests",
      Buffer.buffer(ExampleData.getPriceLoadRequestAsString(ExampleData.millionPricesFileUrl)).toJsonObject(),
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
  public void testPriceLoadByApi(TestContext tc){
    Async async = tc.async();
    WebClient httpClient = WebClient.create(vertx);
    httpClient.post(8080, "localhost", "/prices/load-from-url")
      .putHeader("content-type", "application/json")
      .sendBuffer(Buffer.buffer(ExampleData.getPriceLoadRequestAsString(ExampleData.millionPricesFileUrl)), asyncResult -> {
        if (asyncResult.succeeded()) {
          HttpResponse<Buffer> response = asyncResult.result();
          tc.assertEquals(202,response.statusCode());
        } else {
          tc.fail("calling submit price load api failed totally");
        }
        async.complete();
      });
  }

}

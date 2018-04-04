package com.commercetools.bulkpricer;

import com.commercetools.bulkpricer.helpers.JsonUtils;
import com.commercetools.bulkpricer.messages.JsonBusMessage;
import com.commercetools.bulkpricer.messages.JsonBusMessageCodec;
import com.commercetools.bulkpricer.messages.PriceLookUpRequest;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(VertxUnitRunner.class)
public class JsonTest {

  private final Logger logger = LoggerFactory.getLogger(BulkPriceHttpApi.class);

  private Vertx vertx;

  @Before
  public void setUp(TestContext tc) {
    vertx = Vertx.vertx();
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void JsonBusMessageTest(TestContext tc) {
    Async async = tc.async();
    vertx.eventBus().registerDefaultCodec(JsonBusMessage.class, new JsonBusMessageCodec());

    MessageConsumer<JsonNode> consumer = vertx.eventBus().consumer("serialization-test", receivedMessage ->{
      tc.assertEquals(JsonUtils.toJsonString(PriceLookUpRequest.message(
        Arrays.asList("test-group-1"),
        Arrays.asList("test-sku"),
        "EUR")),
        JsonUtils.toJsonString(receivedMessage.body()));
      async.complete();
    });
    vertx.eventBus().publish("serialization-test", PriceLookUpRequest.message(
      Arrays.asList("test-group-1"),
      Arrays.asList("test-sku"),
      "EUR"));
  }

}

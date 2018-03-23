package com.commercetools.bulkpricer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.stream.Stream;

public class BulkPriceLoader extends AbstractVerticle {

  private MessageConsumer<String> loadRequestsConsumer;

  @Override
  public void start() {
    loadRequestsConsumer = vertx.eventBus().consumer("bulkpricer.loadrequests", this::handleLoadRequest);
  }

  @Override
  public void stop() {
    loadRequestsConsumer.unregister();
    // TODO free memory of price lists?
  }

  private void handleLoadRequest(Message<String> message) {
    // TODO parse into PriceLoadRequest object
    JsonObject params = new JsonObject(message.body());
    String correlationId = message.headers().get("X-Correlation-ID");
    DeliveryOptions msgOptions = new DeliveryOptions().addHeader("X-Correlation-ID", correlationId);

    String groupKey = params.getString("groupKey");
    CurrencyUnit currency = Monetary.getCurrency(params.getString("currencyCode", "EUR"));
    String fileURI = params.getString("fileURI");
    if (fileURI == null || groupKey == null) {
      message.reply(new JsonObject().put("status", 400)
        .put("message", "inssufficient parameters - groupkey and fileURI are mandatory"), msgOptions);
    } else {
      message.reply(new JsonObject().put("status", 202)
        .put("message", "accepted import job"), msgOptions);
      LocalMap<String, ShareablePriceList> sharedPrices = vertx.sharedData().getLocalMap("prices");
      try {
        sharedPrices.put(groupKey, new ShareablePriceList(readRemotePrices(fileURI,currency), currency));
      } catch (IOException e) {
        vertx.eventBus().send("bulkpricer.loadresults", new JsonObject().put("status", 500)
          .put("message", "IO error loading remote price list")
          .put("messageDetails", e.getMessage()), msgOptions);
      } catch (ParseException e) {
        vertx.eventBus().send("bulkpricer.loadresults", new JsonObject().put("status", 500)
          .put("message", "could not parse remote price list")
          .put("messageDetails", e.getMessage()), msgOptions);
      }
      vertx.eventBus().send("bulkpricer.loadresults", new JsonObject().put("status", 200)
        .put("message", "successfully loaded price list"), msgOptions);
    }
  }

  private IntIntHashMap readRemotePrices(String fileURI, CurrencyUnit currency) throws IOException, ParseException{
    IntIntHashMap prices = new IntIntHashMap();

    InputStream remoteStream = new URL(fileURI).openConnection().getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(remoteStream));
    Stream<String> lines = reader.lines();
    // using the iterable to pass the exception up
    for(String line : (Iterable<String>)lines::iterator){
      prices.putPair(parseLine(line, currency));
    }
    return prices;
  }

  private IntIntPair parseLine(String line, CurrencyUnit currency) throws ParseException{
    int separatorPosition = line.indexOf(",");
    String keyStr = line.substring(0,separatorPosition);
    String valStr = line.substring(separatorPosition + 1, line.length());

    int sku = NumberFormat.getIntegerInstance().parse(keyStr).intValue();
    MonetaryAmount price = FastMoney.of(NumberFormat.getCurrencyInstance().parse(valStr), currency);
    int centAmount = new MoneyRepresentation(price).getCentAmount();
    return PrimitiveTuples.pair(sku, centAmount);
  }
}


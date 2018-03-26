package com.commercetools.bulkpricer;

import com.commercetools.bulkpricer.apimodel.MoneyRepresentation;
import com.commercetools.bulkpricer.helpers.MemoryUsage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
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
import java.util.Locale;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public class BulkPriceLoader extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(BulkPricer.class);

  private MessageConsumer<String> loadRequestsConsumer;

  // contains sku strings mapped to themselves. used for string deduplication
  // motivation: goal of this service is to hold large numbers of price lists for the same sku.
  // the maps holding the prices as integers keep references to the sku Strings.
  // relying on G1 string deduplication works "only if"
  // TODO: measure actual impact
  private WeakHashMap<String, String> skuStringStore = new WeakHashMap<>();

  @Override
  public void start() {
    loadRequestsConsumer = vertx.eventBus().consumer("bulkpricer.loadrequests", this::handleLoadRequest);
  }

  @Override
  public void stop() {
    loadRequestsConsumer.unregister();
    this.skuStringStore.clear();
  }

  private void handleLoadRequest(Message<String> message) {
    // TODO parse into PriceLoadRequest object
    JsonObject params = new JsonObject(message.body());
    String correlationId = message.headers().get("X-Correlation-ID");
    if (correlationId == null) correlationId = "bulkpricer-" + UUID.randomUUID().toString();
    DeliveryOptions msgOptions = new DeliveryOptions().addHeader("X-Correlation-ID", correlationId);

    String groupKey = params.getString("groupKey");
    CurrencyUnit currency = Monetary.getCurrency(params.getString("currencyCode", "EUR"));
    String fileURL = params.getString("fileURL");
    if (fileURL == null || groupKey == null) {
      message.reply(new JsonObject()
        .put("statusCode", 400)
        .put("statusMessage", "inssufficient parameters - groupkey and fileURL are mandatory"), msgOptions);
    } else {
      message.reply(new JsonObject()
        .put("statusCode", 202)
        .put("statusMessage", "accepted import job"), msgOptions);

      // executeBlocking without further parameters implies serial execution in a worker thread.
      // this is the desired behavior to prevent a) event loop blocking b) multiple temporary price lists hogging memory.
      vertx.<ShareablePriceList>executeBlocking(ebFuture -> {
        ShareablePriceList newPriceList = readRemotePrices(fileURL, currency, groupKey);
        if (newPriceList.getLoadStatus() == 201) {
          LocalMap<String, ShareablePriceList> sharedPrices = vertx.sharedData().getLocalMap("prices");
          sharedPrices.put(groupKey, newPriceList);
        }
        // TODO save the group metadata locally or e.g. in CTP custom objects to be able to recover state after restart
        ebFuture.complete(newPriceList);
      }, res -> {
        if(res.succeeded()){
          vertx.eventBus().send("bulkpricer.loadresults", new JsonObject()
              .put("statusCode", res.result().getLoadStatus())
              .put("statusMessage", res.result().getLoadStatusMessage())
            , msgOptions);
        }
      });

    }
  }

  public ShareablePriceList readRemotePrices(String fileURL, CurrencyUnit currency, String groupKey) {
    try {
      long memoryBefore = MemoryUsage.getUsedMb();
      MutableObjectIntMap<String> prices = new ObjectIntHashMap<>();
      InputStream remoteStream = null;
      remoteStream = new URL(fileURL).openConnection().getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(remoteStream));
      Stream<String> lines = reader.lines();
      Integer duplicateSkuCount = 0;
      for (String line : (Iterable<String>) lines::iterator) {
        ObjectIntPair<String> pair = parseLine(line, currency);
        if (prices.containsKey(pair.getOne())) {
          duplicateSkuCount++;
        } else {
          prices.putPair(pair);
        }
      }
      logger.info("loaded price list, used memory difference: " + (MemoryUsage.getUsedMb() - memoryBefore));
      logger.info(MemoryUsage.memoryReport());
      return new ShareablePriceList(groupKey, prices, currency, duplicateSkuCount, 201, "loaded price list from " + fileURL);
    } catch (IOException e) {
      return new ShareablePriceList(groupKey,null, currency, null, 500, "IO error loading remote price list " + e.getMessage());
    } catch (ParseException e) {
      return new ShareablePriceList(groupKey,null, currency, null, 500, "could not parse remote price list " + e.getMessage());
    }
  }

  public ObjectIntPair<String> parseLine(String line, CurrencyUnit currency) throws ParseException {
    int separatorPosition = line.indexOf(",");
    String sku = cheapSku(line.substring(0, separatorPosition));
    String valStr = line.substring(separatorPosition + 1, line.length());

    MonetaryAmount price = FastMoney.of(NumberFormat.getNumberInstance(Locale.US).parse(valStr), currency);
    int centAmount = new MoneyRepresentation(price).getCentAmount();
    return PrimitiveTuples.pair(sku, centAmount);
  }

  private String cheapSku(String sku) {
    if (skuStringStore.containsKey(sku)) {
      return skuStringStore.get(sku);
    } else {
      skuStringStore.put(sku, sku);
      return sku;
    }
  }


}


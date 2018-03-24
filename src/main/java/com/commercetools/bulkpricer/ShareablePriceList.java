package com.commercetools.bulkpricer;

import io.vertx.core.shareddata.Shareable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;

import javax.money.CurrencyUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ShareablePriceList implements Shareable {

  private ImmutableObjectIntMap<String> prices;
  private Date createdAt;
  private CurrencyUnit currency;
  private Integer duplicateSkuCount;

  public ShareablePriceList(MutableObjectIntMap<String> prices, CurrencyUnit currency, Integer duplicateSkuCount) {
    this.prices = prices.toImmutable();
    createdAt = new Date();
    this.currency = currency;
    this.duplicateSkuCount = duplicateSkuCount;
  }

  public ImmutableObjectIntMap<String> getPrices() {
    return prices;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public CurrencyUnit getCurrency() {
    return currency;
  }

  public Integer getDuplicateSkuCount() { return duplicateSkuCount; }

  public static String getDateAsUtcIso8601(Date date){
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    return df.format(date);
  }
}

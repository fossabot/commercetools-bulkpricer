package com.commercetools.bulkpricer;

import io.vertx.core.shareddata.Shareable;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ShareablePriceList implements Shareable {

  private ImmutableIntIntMap prices;
  private Date createdAt;

  public ShareablePriceList(MutableIntIntMap load) {
    prices = load.toImmutable();
    createdAt = new Date();
  }

  public ImmutableIntIntMap getPrices() {
    return prices;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public static String getDateAsUtcIso8601(Date date){
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    return df.format(date);
  }
}

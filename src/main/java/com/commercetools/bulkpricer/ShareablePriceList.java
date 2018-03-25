package com.commercetools.bulkpricer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.shareddata.Shareable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;

import javax.money.CurrencyUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ShareablePriceList implements Shareable {

  private String groupKey;
  private ImmutableObjectIntMap<String> prices;
  private Date createdAt;
  private CurrencyUnit currency;
  private Integer duplicateSkuCount;
  private Integer loadStatus;
  private String loadStatusMessage;

  public ShareablePriceList(String groupKey, MutableObjectIntMap<String> prices, CurrencyUnit currency, Integer duplicateSkuCount, Integer loadStatus, String loadStatusMessage) {
    this.groupKey = groupKey;
    this.prices = prices.toImmutable();
    createdAt = new Date();
    this.currency = currency;
    this.duplicateSkuCount = duplicateSkuCount;
    this.loadStatus = loadStatus;
    this.loadStatusMessage = loadStatusMessage;
  }

  public String getGroupKey() {
    return groupKey;
  }

  @JsonIgnore
  public ImmutableObjectIntMap<String> getPrices() {
    return prices;
  }

  @JsonProperty("createdAt")
  public String getCreatedAtAsISO() {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    return df.format(createdAt);
  }

  @JsonIgnore
  public Date getCreatedAt() {
    return createdAt;
  }

  @JsonIgnore
  public CurrencyUnit getCurrency() {
    return currency;
  }

  public String getCurrencyCode(){
    return currency.getCurrencyCode();
  }

  public Integer getDuplicateSkuCount() {
    return duplicateSkuCount;
  }

  @JsonIgnore
  public Integer getLoadStatus() {
    return loadStatus;
  }

  @JsonIgnore
  public String getLoadStatusMessage() {
    return loadStatusMessage;
  }

  @JsonProperty("uniqueSkuCount")
  public Integer getSize(){
    return prices.size();
  }

}

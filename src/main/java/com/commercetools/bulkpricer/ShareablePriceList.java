package com.commercetools.bulkpricer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.shareddata.Shareable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
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
  private String fileURL;
  private Integer loadStatus;
  private String loadStatusMessage;

  public ShareablePriceList(String groupKey, MutableObjectIntMap<String> prices, CurrencyUnit currency, Integer duplicateSkuCount, String fileURL, Integer loadStatus, String loadStatusMessage) {
    this.groupKey = groupKey;
    this.prices = prices.toImmutable();
    createdAt = new Date();
    this.currency = currency;
    this.duplicateSkuCount = duplicateSkuCount;
    this.fileURL = fileURL;
    this.loadStatus = loadStatus;
    this.loadStatusMessage = loadStatusMessage;
  }

  @JsonCreator
  public ShareablePriceList(@JsonProperty("groupKey") String groupKey, @JsonProperty("currencyCode") String currencyCode, @JsonProperty("duplicateSkuCount") Integer duplicateSkuCount, @JsonProperty("fileURL") String fileURL) {
    this.groupKey = groupKey;
    this.currency = Monetary.getCurrency(currencyCode);
    this.duplicateSkuCount = duplicateSkuCount;
    this.fileURL = fileURL;
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
    if(createdAt != null) {
      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
      df.setTimeZone(tz);
      return df.format(createdAt);
    }else{
      return null;
    }
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

  public String getFileURL(){ return fileURL; }

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
    if(prices != null){
      return prices.size();
    }else{
      return null;
    }
  }

}

package com.commercetools.bulkpricer.apimodel;

import javax.money.MonetaryAmount;

public class PriceResponse {
  private MonetaryAmount amount;
  private String groupKey;
  private String sku;
  private String lastModified;

  public PriceResponse(MonetaryAmount amount, String groupKey, String sku, String lastModified){
    this.amount = amount;
    this.groupKey = groupKey;
    this.sku = sku;
    this.lastModified = lastModified;
  }

  public MonetaryAmount getAmount() {
    return amount;
  }

  public String getGroupKey() {
    return groupKey;
  }

  public String getSku() {
    return sku;
  }

  public String getLastModified() {
    return lastModified;
  }
}

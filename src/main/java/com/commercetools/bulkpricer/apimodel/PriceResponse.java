package com.commercetools.bulkpricer.apimodel;

import io.sphere.sdk.products.Price;

import javax.money.MonetaryAmount;

public class PriceResponse {
  private Price price;
  private String groupKey;
  private String sku;
  private String lastModified;

  public PriceResponse(MonetaryAmount amount, String groupKey, String sku, String lastModified){
    price = Price.of(amount);
    this.groupKey = groupKey;
    this.sku = sku;
    this.lastModified = lastModified;
  }

  public Price getPrice() {
    return price;
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

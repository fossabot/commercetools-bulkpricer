package com.commercetools.bulkpricer.messages;

import java.util.List;

public class PriceLookUpRequest extends AbstractBusMessage {

  public List<String> groupKeys;
  public List<String> skus;
  public String currencyCode;

  public static PriceLookUpRequest of(List<String> groupKeys, List<String> skus, String currencyCode) {
    PriceLookUpRequest me = new PriceLookUpRequest();
    me.groupKeys = groupKeys;
    me.skus = skus;
    me.currencyCode = currencyCode;
    return me;
  }

}

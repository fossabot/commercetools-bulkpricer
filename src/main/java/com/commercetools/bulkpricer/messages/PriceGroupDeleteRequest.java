package com.commercetools.bulkpricer.messages;

public class PriceGroupDeleteRequest extends AbstractBusMessage {
  public String groupKey;
  public PriceGroupDeleteRequest(String groupKey){
    this.groupKey = groupKey;
  }
}

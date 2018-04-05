package com.commercetools.bulkpricer.messages;

public class PriceLoadRequest extends AbstractBusMessage{
  public String groupKey;
  public String currencyCode;
  public String fileURL;
}

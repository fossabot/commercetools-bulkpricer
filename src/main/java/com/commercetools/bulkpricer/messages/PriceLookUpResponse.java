package com.commercetools.bulkpricer.messages;

import java.util.Map;

public class PriceLookUpResponse extends AbstractBusMessage{
  public Map<String, Map<String, Integer>> prices;

  public PriceLookUpResponse() {
  }

  public static PriceLookUpResponse of(Map<String, Map<String, Integer>> prices){
    PriceLookUpResponse me = new PriceLookUpResponse();
    me.prices = prices;
    return me;
  }

}

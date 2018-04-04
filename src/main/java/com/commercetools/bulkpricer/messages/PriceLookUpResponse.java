package com.commercetools.bulkpricer.messages;

import java.util.Map;

public class PriceLookUpResponse {
  public Map<String, Map<String, Integer>> prices;

  public PriceLookUpResponse() {
  }

  public static PriceLookUpResponse of(Map<String, Map<String, Integer>> prices){
    PriceLookUpResponse me = new PriceLookUpResponse();
    me.prices = prices;
    return me;
  }

  public static JsonBusMessage<PriceLookUpResponse> message(Map<String /* groupKey */, Map<String /* sku */, Integer /* centAmount */>> prices) {
    return new JsonBusMessage<PriceLookUpResponse>().withPayload(PriceLookUpResponse.of(prices));
  }

}

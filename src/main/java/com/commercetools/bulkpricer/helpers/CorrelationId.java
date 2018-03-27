package com.commercetools.bulkpricer.helpers;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

import java.util.UUID;

public class CorrelationId {

  public static String headerName = "X-Correlation-ID";

  public static String generate(){
    return "bulkpricer-" + UUID.randomUUID().toString();
  }

  public static String getIfNotPresentInMessage(Message message){
    String correlationId = message.headers().get(headerName);
    if (correlationId == null) correlationId = generate();
    return correlationId;
  }

  public static DeliveryOptions addIfNotPresent(DeliveryOptions deliveryOptions){
    if(!deliveryOptions.getHeaders().contains(headerName)){
      return deliveryOptions.addHeader(headerName, generate());
    }else{
      return deliveryOptions;
    }
  }

  public static DeliveryOptions generateDeliveryOptions(){
    return new DeliveryOptions().addHeader(headerName, generate());
  }
}

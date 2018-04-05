package com.commercetools.bulkpricer.helpers;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class CorrelationId {

  public static String headerName = "X-Correlation-ID";

  public static String generate(){
    return "bulkpricer-" + UUID.randomUUID().toString();
  }

  public static String generateIfNotPresentInMessage(Message message){
    String correlationId = message.headers().get(headerName);
    if (correlationId == null) correlationId = generate();
    return correlationId;
  }

  public static DeliveryOptions getDeliveryOptions(){
    return new DeliveryOptions().addHeader(headerName, generate());
  }

  public static DeliveryOptions getDeliveryOptions(Message message){
    return new DeliveryOptions().addHeader(headerName, generateIfNotPresentInMessage(message));
  }

  public static DeliveryOptions getDeliveryOptions(RoutingContext routingContext){
    String correlationId = routingContext.request().headers().get(headerName);
    if (correlationId == null) correlationId = generate();
    return new DeliveryOptions().addHeader(headerName, correlationId);
  }

  public static DeliveryOptions addIfNotPresent(DeliveryOptions deliveryOptions){
    if(!deliveryOptions.getHeaders().contains(headerName)){
      return deliveryOptions.addHeader(headerName, generate());
    }else{
      return deliveryOptions;
    }
  }

}

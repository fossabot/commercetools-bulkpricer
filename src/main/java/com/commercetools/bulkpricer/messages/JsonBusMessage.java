package com.commercetools.bulkpricer.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonBusMessage<T> {

  public JsonBusMessage(){

  }
  public JsonBusMessage(T payload){
    this.payload = payload;
  }

  @JsonCreator
  public JsonBusMessage(@JsonProperty("payload") T payload, @JsonProperty("correlationId") String correlationId){
    this.payload = payload;
    this.correlationId = correlationId;
  }

  public String correlationId;

  public String getType(){
    return payload.getClass().getSimpleName();
  }

  public T payload;

  public JsonBusMessage<T> withPayload(T payload){
    this.payload = payload;
    return this;
  }

  public JsonBusMessage<T> withCorrelationId(String correlationId){
    this.correlationId = correlationId;
    return this;
  }

}

package com.commercetools.bulkpricer.messages;

import io.vertx.core.json.JsonObject;

public abstract class AbstractBusMessage {

  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }
}

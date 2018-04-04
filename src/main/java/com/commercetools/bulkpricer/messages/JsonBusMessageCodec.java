package com.commercetools.bulkpricer.messages;

import com.commercetools.bulkpricer.helpers.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class JsonBusMessageCodec implements MessageCodec<JsonBusMessage, JsonBusMessage> {

  @Override
  public void encodeToWire(Buffer buffer, JsonBusMessage busMessage) {
    buffer.appendString(JsonUtils.toJsonString(busMessage));
  }

  @Override
  public JsonBusMessage decodeFromWire(int pos, Buffer buffer) {
    JsonNode json = JsonUtils.parseToJsonNode(buffer.getString(pos, buffer.length()));
    try {
      Class payloadClass = Class.forName(JsonBusMessage.class.getPackage().getName() + "." + json.get("type").asText());
      return JsonUtils.readObject(
        json,
        JsonUtils.getCtpObjectMapper().getTypeFactory().constructParametricType(JsonBusMessage.class, payloadClass)
      );
    } catch (ClassNotFoundException e) {
      return JsonUtils.readObject(json, JsonBusMessage.class);
    }
  }

  @Override
  public JsonBusMessage transform(JsonBusMessage busMessage) {
    return busMessage;
  }

  @Override
  public String name() {
    return "JsonBusMessageCodec";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}

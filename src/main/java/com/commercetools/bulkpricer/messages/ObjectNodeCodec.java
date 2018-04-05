package com.commercetools.bulkpricer.messages;

import com.commercetools.bulkpricer.helpers.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/*
*  UNUSED - MAKES MORE PROBLEMS THAN IT HELPS
*
* A vert.x bus codec that allows passing Jackson's own JsonNode's instead of the vert.x specific
* JsonObjects in implementations that cannot work with the vert.x provided ObjectMapper but need own
* object mapping.
*/
public class ObjectNodeCodec implements MessageCodec<ObjectNode, ObjectNode> {

  @Override
  public void encodeToWire(Buffer buffer, ObjectNode busMessage) {
    try {
      buffer.appendBytes(JsonUtils.objectMapper.writeValueAsBytes(busMessage));
    } catch (JsonProcessingException e) {
      // should not happen since only allowed input type is JsonNode which is serializable by definition.
      e.printStackTrace();
    }
  }

  @Override
  public ObjectNode decodeFromWire(int pos, Buffer buffer) {
    return JsonUtils.parseObjectNode(buffer.getString(pos, buffer.length()));
  }

  @Override
  public ObjectNode transform(ObjectNode busMessage) {
    return busMessage;
  }

  @Override
  public String name() {
    return this.getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}


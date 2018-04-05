package com.commercetools.bulkpricer.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.sphere.sdk.json.SphereJsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/*
 * A set of static convenience methods to work with JSON using the commercetools JVM SDK provided
 * JSON serialization / deserialization only to work with CTP consistent JSON models for common types
 * like money, dates etc. and to directly work with vert.x Buffers
 *
 * Ugly: to be able to provide the vert.x JsonObjects but use the CTP object mapper double
 * object mapping and intermediate serialization is necessary
 */
public class JsonUtils {

  public static final ObjectMapper objectMapper = SphereJsonUtils.newObjectMapper();

  public static <T> T readObject(final String jsonAsString, final Class<T> clazz) {
    return SphereJsonUtils.readObject(jsonAsString, clazz);
  }

  public static <T> T readObject(final JsonNode jsonNode, final Class<T> clazz) {
    return SphereJsonUtils.readObject(jsonNode, clazz);
  }

  public static <T> T readObject(Message<JsonObject> message, final Class<T> clazz) {
    // this is pretty pretty ugly - need to go through deserialization and re-serialization
    return readObject(message.body().encode(), clazz);
  }

  public static ObjectNode parseObjectNode(String json){
    return (ObjectNode)SphereJsonUtils.parse(json);
  }

  public static JsonObject parseJsonObject(String json){
    return JsonObject.mapFrom(SphereJsonUtils.parse(json));
  }

  public static ObjectNode parseObjectNode(Buffer json){
    return (ObjectNode)SphereJsonUtils.parse(json.getBytes());
  }

  public static JsonObject parseJsonObject(Buffer json){
    return JsonObject.mapFrom(SphereJsonUtils.parse(json.getBytes()));
  }

  public static ObjectNode mapToObjectNode(Object object) {
    return (ObjectNode)SphereJsonUtils.toJsonNode(object);
  }

  public static JsonObject mapToJsonObject(Object object) {
    return JsonObject.mapFrom(SphereJsonUtils.toJsonNode(object));
  }

  public static String toJsonString(Object obj) {
    return SphereJsonUtils.toJsonString(obj);
  }

}

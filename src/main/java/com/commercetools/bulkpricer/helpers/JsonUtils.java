package com.commercetools.bulkpricer.helpers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.sphere.sdk.json.SphereJsonUtils;
import io.vertx.core.eventbus.Message;

/*
A set of static convenience methods to work with JSON using the commercetools JVM SDK provided
JSON serialization / deserialization only to work with CTP consistent JSON models for common types
like money, dates etc.
 */
public class JsonUtils {
  public static ObjectMapper getCtpObjectMapper(){
    return SphereJsonUtils.newObjectMapper();
  }

  public static <T> T readObject(final String jsonAsString, final Class<T> clazz) {
    return SphereJsonUtils.readObject(jsonAsString,clazz);
  }

  public static <T> T readObject(final JsonNode jsonNode, final Class<T> clazz) {
    return SphereJsonUtils.readObject(jsonNode,clazz);
  }

  public static <T> T readObject(final JsonNode jsonNode, final JavaType javaType) {
    return SphereJsonUtils.readObject(jsonNode, javaType);
  }

  public static <T> T readObject(Message<JsonNode> message, final Class<T> clazz) {
    return readObject(message.body(),clazz);
  }

  public static JsonNode parseToJsonNode(String jsonAsString){
    return SphereJsonUtils.parse(jsonAsString);
  }

  public static JsonNode mapToJsonNode(Object object){
    return SphereJsonUtils.toJsonNode(object);
  }

  public static String toJsonString(Object obj){
    return SphereJsonUtils.toJsonString(obj);
  }

  // an improvement for own models, but not suitable map CT JVM SDK models:
  public static void configureVertxMappers(ObjectMapper vertxMapper, ObjectMapper vertxPrettyMapper){
    vertxMapper.registerModule(new ParameterNamesModule());
    vertxPrettyMapper.registerModule(new ParameterNamesModule());
  }

}

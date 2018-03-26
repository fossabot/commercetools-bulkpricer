package com.commercetools.bulkpricer.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.sphere.sdk.json.SphereJsonUtils;

public class JsonUtils {
  public static ObjectMapper getCtpObjectMapper(){
    return SphereJsonUtils.newObjectMapper();
  }

  // this is an improvement for own models, but not suitable map CT JVM SDK models:
  public static void configureVertxMappers(ObjectMapper vertxMapper, ObjectMapper vertxPrettyMapper){
    vertxMapper.registerModule(new ParameterNamesModule());
    vertxPrettyMapper.registerModule(new ParameterNamesModule());
  }

  public static <T> T readObject(final String jsonAsString, final Class<T> clazz) {
    return SphereJsonUtils.readObject(jsonAsString,clazz);
  }

  public static String toJsonString(Object obj){
    return SphereJsonUtils.toJsonString(obj);
  }
}

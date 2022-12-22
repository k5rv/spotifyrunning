package com.ksaraev.spotifyrunning.client.config.encoders;

import com.google.common.collect.Maps;
import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestedObjectQueryMapEncoder implements QueryMapEncoder {

  private final Map<Class<?>, NestedObjectQueryMapEncoder.ObjectParamMetadata> classToMetadata =
      new HashMap<>();

  @Override
  public Map<String, Object> encode(Object object) throws EncodeException {
    return encode(object, null);
  }

  private Map<String, Object> encode(Object object, Map<String, Object> fieldNameToValue) {

    if (null == fieldNameToValue) {
      fieldNameToValue = Maps.newHashMap();
    }

    try {
      NestedObjectQueryMapEncoder.ObjectParamMetadata metadata = getMetadata(object.getClass());

      for (Field field : metadata.objectFields) {
        Object value = field.get(object);

        if (value != null && value != object) {
          Param alias = field.getAnnotation(Param.class);

          String name = alias != null ? alias.value() : field.getName();

          ClassLoader classLoader = value.getClass().getClassLoader();

          if (classLoader == null) {
            processNameAndValue(name, value, fieldNameToValue);
          } else {
            encode(value, fieldNameToValue);
          }
        }
      }

      return fieldNameToValue;

    } catch (IllegalAccessException e) {
      throw new EncodeException("Failure encoding object into query map", e);
    }
  }

  private void processNameAndValue(
      String name, Object value, Map<String, Object> fieldNameToValue) {
    fieldNameToValue.put(name, value);
  }

  private NestedObjectQueryMapEncoder.ObjectParamMetadata getMetadata(Class<?> objectType) {
    NestedObjectQueryMapEncoder.ObjectParamMetadata metadata = classToMetadata.get(objectType);

    if (metadata == null) {
      metadata = NestedObjectQueryMapEncoder.ObjectParamMetadata.parseObjectType(objectType);
      classToMetadata.put(objectType, metadata);
    }
    return metadata;
  }

  private record ObjectParamMetadata(List<Field> objectFields) {

    private static NestedObjectQueryMapEncoder.ObjectParamMetadata parseObjectType(Class<?> type) {
      List<Field> allFields = new ArrayList<>();

      for (Class<?> aClass = type; aClass != null; aClass = aClass.getSuperclass()) {
        for (Field field : aClass.getDeclaredFields()) {
          if (!field.isSynthetic()) {
            field.setAccessible(true);
            allFields.add(field);
          }
        }
      }
      return new NestedObjectQueryMapEncoder.ObjectParamMetadata(allFields);
    }
  }
}

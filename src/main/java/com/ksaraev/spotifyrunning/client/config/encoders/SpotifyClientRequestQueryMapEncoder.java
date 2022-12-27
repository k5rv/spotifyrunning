package com.ksaraev.spotifyrunning.client.config.encoders;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SpotifyClientRequestQueryMapEncoder implements QueryMapEncoder {

  private final Map<Class<?>, SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata>
      classToMetadata = new HashMap<>();

  @Override
  public Map<String, Object> encode(Object object) throws EncodeException {
    return encode(object, null);
  }

  private Map<String, Object> encode(Object object, Map<String, Object> fieldNameToValue) {

    if (Objects.isNull(fieldNameToValue)) {
      fieldNameToValue = Maps.newHashMap();
    }

    try {
      SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata metadata =
          getMetadata(object.getClass());

      for (Field field : metadata.objectFields) {
        Object value = field.get(object);

        if (value != null && value != object) {
          Param alias = field.getAnnotation(Param.class);

          String name =
              alias != null
                  ? alias.value()
                  : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());

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
    if (Objects.nonNull(value) && Collection.class.isAssignableFrom(value.getClass())) {
      value =
          ((Collection<?>) value).stream().map(String::valueOf).collect(Collectors.joining(","));
    }
    fieldNameToValue.put(name, value);
  }

  private SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata getMetadata(Class<?> objectType) {
    SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata metadata =
        classToMetadata.get(objectType);
    if (Objects.isNull(metadata)) {
      metadata =
          SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata.parseObjectType(objectType);
      classToMetadata.put(objectType, metadata);
    }
    return metadata;
  }

  private record ObjectParamMetadata(List<Field> objectFields) {

    private static SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata parseObjectType(
        Class<?> type) {
      List<Field> allFields = new ArrayList<>();
      for (Class<?> aClass = type; aClass != null; aClass = aClass.getSuperclass()) {
        for (Field field : aClass.getDeclaredFields()) {
          if (!field.isSynthetic()) {
            field.setAccessible(true);
            allFields.add(field);
          }
        }
      }
      return new SpotifyClientRequestQueryMapEncoder.ObjectParamMetadata(allFields);
    }
  }
}

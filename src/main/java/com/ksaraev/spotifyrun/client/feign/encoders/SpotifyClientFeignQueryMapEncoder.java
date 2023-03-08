package com.ksaraev.spotifyrun.client.feign.encoders;

import static com.ksaraev.spotifyrun.client.exceptions.SpotifyClientRequestEncodingException.UNABLE_TO_ENCODE_OBJECT_INTO_QUERY_MAP;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.ksaraev.spotifyrun.client.exceptions.SpotifyClientRequestEncodingException;
import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SpotifyClientFeignQueryMapEncoder implements QueryMapEncoder {

  private final Map<Class<?>, ObjectParamMetadata> classToMetadata = new HashMap<>();

  @Override
  public Map<String, Object> encode(Object object) throws EncodeException {
    return encode(object, null);
  }

  Map<String, Object> encode(Object object, Map<String, Object> fieldNameToValue) {
    try {

      if (fieldNameToValue == null) fieldNameToValue = Maps.newHashMap();

      ObjectParamMetadata metadata = getMetadata(object.getClass());

      for (Field field : metadata.objectFields) {
        Object value = field.get(object);

        if (value != null && value != object) {
          Param alias = field.getAnnotation(Param.class);

          String name =
              alias != null
                  ? alias.value()
                  : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());

          Class<?> currentClass = value.getClass();

          if (currentClass.getClassLoader() == null || currentClass.isEnum()) {
            processNameAndValue(name, value, fieldNameToValue);
          } else {
            encode(value, fieldNameToValue);
          }
        }
      }

      return fieldNameToValue;

    } catch (IllegalAccessException | RuntimeException e) {
      throw new SpotifyClientRequestEncodingException(
          UNABLE_TO_ENCODE_OBJECT_INTO_QUERY_MAP + e.getMessage(), e);
    }
  }

  private void processNameAndValue(
      String name, Object value, Map<String, Object> fieldNameToValue) {

    if (ObjectUtils.isEmpty(name) || ObjectUtils.isEmpty(value)) {
      return;
    }

    if (Collection.class.isAssignableFrom(value.getClass())) {
      Object joinValue =
          ((Collection<?>) value).stream().map(String::valueOf).collect(Collectors.joining(","));
      fieldNameToValue.put(name, joinValue);
      return;
    }

    fieldNameToValue.put(name, value);
  }

  private ObjectParamMetadata getMetadata(Class<?> objectType) {
    ObjectParamMetadata metadata = classToMetadata.get(objectType);
    if (metadata == null) {
      metadata = ObjectParamMetadata.parseObjectType(objectType);
      classToMetadata.put(objectType, metadata);
    }
    return metadata;
  }

  private record ObjectParamMetadata(List<Field> objectFields) {

    private static ObjectParamMetadata parseObjectType(Class<?> type) {
      List<Field> allFields = new ArrayList<>();
      for (Class<?> currentClass = type;
          currentClass != null;
          currentClass = currentClass.getSuperclass()) {
        for (Field field : currentClass.getDeclaredFields()) {
          if (!field.isSynthetic()) {
            field.setAccessible(true);
            allFields.add(field);
          }
        }
      }
      return new ObjectParamMetadata(allFields);
    }
  }
}

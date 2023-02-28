package com.ksaraev.spotifyrun.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.util.AssertionErrors;

public class JsonHelper {
  public static <T> T jsonToObject(String json, Class<T> aClass) {
    try {
      return new ObjectMapper().readValue(json, aClass);
    } catch (JsonProcessingException e) {
      AssertionErrors.fail(
          "Fail to convert Json [" + json + "] to instance of [" + aClass + "]:" + e.getMessage());
      return null;
    }
  }
}

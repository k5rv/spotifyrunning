package com.ksaraev.spotify.model;

import lombok.experimental.StandardException;

@StandardException
public class MappingSourceIsNullException extends RuntimeException {
  public static final String MAPPING_SOURCE_IS_NULL = "Mapping source is null";

  protected MappingSourceIsNullException() {
    super(MAPPING_SOURCE_IS_NULL);
  }
}

package com.ksaraev.spotifyrun.exception.mapping;

import lombok.experimental.StandardException;

@StandardException
public class MappingSourceIsNullException extends MappingException {
  public static final String MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE = "Mapping source is null";
}

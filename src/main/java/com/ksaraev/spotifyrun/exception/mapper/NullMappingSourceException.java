package com.ksaraev.spotifyrun.exception.mapper;

import lombok.experimental.StandardException;

@StandardException
public class NullMappingSourceException extends MappingException {
  public static final String MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE = "Mapping source is null";
}

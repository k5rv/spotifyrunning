package com.ksaraev.spotifyrun.exception.mapping;

import lombok.experimental.StandardException;

@StandardException
public class MappingException extends RuntimeException {

  public static final String MAPPING_ERROR_MESSAGE = "Error during mapping";
}

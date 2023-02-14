package com.ksaraev.spotifyrun.exception;

import lombok.experimental.StandardException;

@StandardException
public class MappingException extends RuntimeException {
  public static final String SPOTIFY_USER_PROFILE_MAPPING_ERROR =
      "Error while mapping Spotify User Profile to User";
}

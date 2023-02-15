package com.ksaraev.spotifyrun.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetUserException extends ApplicationException {
  public static final String GET_USER_EXCEPTION_MESSAGE = "Unable to get user";
  public static final String SPOTIFY_USER_PROFILE_IS_NULL = "Spotify user profile is null";
}

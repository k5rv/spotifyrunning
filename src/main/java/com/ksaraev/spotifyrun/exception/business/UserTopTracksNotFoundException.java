package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class UserTopTracksNotFoundException extends RuntimeException {
  public static final String USER_TOP_TRACKS_NOT_FOUND = "User top tracks not found";
}

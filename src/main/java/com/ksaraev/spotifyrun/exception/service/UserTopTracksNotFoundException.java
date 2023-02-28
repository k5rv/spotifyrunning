package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class UserTopTracksNotFoundException extends ServiceException {
  public static final String USER_TOP_TRACKS_NOT_FOUND_MESSAGE = "User top tracks not found";
}

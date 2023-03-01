package com.ksaraev.spotifyrun.exception.controller;

import lombok.experimental.StandardException;

@StandardException
public class UserTopTracksNotFoundException extends ControllerException {
  public static final String USER_TOP_TRACKS_NOT_FOUND = "User top tracks not found";
}

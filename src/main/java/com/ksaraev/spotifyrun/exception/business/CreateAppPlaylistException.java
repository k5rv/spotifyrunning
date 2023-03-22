package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class CreateAppPlaylistException extends RuntimeException {
  public static final String UNABLE_TO_CREATE_APP_PLAYLIST= "Unable to create app playlist: ";
}

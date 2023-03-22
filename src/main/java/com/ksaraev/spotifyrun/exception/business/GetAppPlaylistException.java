package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class GetAppPlaylistException extends RuntimeException {
  public static final String UNABLE_TO_GET_APP_PLAYLIST= "Unable to get app playlist: ";
}

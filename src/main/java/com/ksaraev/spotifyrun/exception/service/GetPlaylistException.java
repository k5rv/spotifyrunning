package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class GetPlaylistException extends RuntimeException {
  public static final String UNABLE_TO_GET_PLAYLIST = "Unable to get playlist: ";
}

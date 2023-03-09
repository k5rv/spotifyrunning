package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class CreatePlaylistException extends RuntimeException {
  public static final String UNABLE_TO_CREATE_PLAYLIST = "Unable to create playlist: ";
}

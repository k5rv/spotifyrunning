package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class AddTracksException extends RuntimeException {
  public static final String UNABLE_TO_ADD_TRACKS = "Unable to add tracks: ";
}

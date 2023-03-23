package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class DeleteTracksException extends RuntimeException {
  public static final String UNABLE_TO_DELETE_TRACKS = "Unable to delete tracks: ";
}

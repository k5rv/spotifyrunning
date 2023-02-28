package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class AddTracksException extends ServiceException {
  public static final String UNABLE_TO_ADD_TRACKS = "Unable to add tracks: ";
}

package com.ksaraev.spotifyrun.client.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyClientReadingErrorResponseException extends SpotifyClientException {
  public static final String UNABLE_TO_READ_ERROR_RESPONSE =
          "Unable to read Spotify API error: ";
}

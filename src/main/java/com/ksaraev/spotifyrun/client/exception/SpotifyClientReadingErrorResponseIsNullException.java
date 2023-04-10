package com.ksaraev.spotifyrun.client.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyClientReadingErrorResponseIsNullException extends SpotifyClientException {
  public static final String READING_ERROR_RESPONSE_IS_NULL =
          "Reading Spotify API error response is null";
}

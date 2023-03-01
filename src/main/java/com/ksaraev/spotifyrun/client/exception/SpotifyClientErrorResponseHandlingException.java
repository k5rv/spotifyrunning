package com.ksaraev.spotifyrun.client.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyClientErrorResponseHandlingException extends SpotifyClientException {
  public static final String ERROR_WHILE_READING_SPOTIFY_API_ERROR_RESPONSE_BODY =
      "Error while reading Spotify API error response body: ";

  public static final String RESPONSE_IS_NULL = "Response is null";
}

package com.ksaraev.spotify.client.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyClientDecodingErrorResponseIsNullException extends SpotifyClientException {

  public static final String DECODING_ERROR_RESPONSE_IS_NULL =
      "Decoding Spotify API error response is null";
}

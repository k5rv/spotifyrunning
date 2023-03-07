package com.ksaraev.spotifyrun.client.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyClientDecodingErrorResponseIsNullException extends SpotifyClientException {

  public static final String DECODING_ERROR_RESPONSE_IS_NULL =
      "Decoding Spotify API error response is null";
}

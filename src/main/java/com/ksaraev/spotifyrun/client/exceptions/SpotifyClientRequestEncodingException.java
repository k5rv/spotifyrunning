package com.ksaraev.spotifyrun.client.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyClientRequestEncodingException extends SpotifyClientException {

  public static final String UNABLE_TO_ENCODE_OBJECT_INTO_QUERY_MAP =
      "Unable to encode object into query map: ";
}

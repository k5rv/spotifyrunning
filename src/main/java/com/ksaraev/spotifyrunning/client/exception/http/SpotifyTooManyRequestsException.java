package com.ksaraev.spotifyrunning.client.exception.http;

public class SpotifyTooManyRequestsException extends RuntimeException {

  public SpotifyTooManyRequestsException(String message) {
    super(message);
  }
}

package com.ksaraev.spotifyrunning.client.exception.http;

public class SpotifyBadRequestException extends RuntimeException {

  public SpotifyBadRequestException(String message) {
    super(message);
  }
}

package com.ksaraev.spotifyrunning.client.exception.http;

public class SpotifyBadGatewayException extends RuntimeException {

  public SpotifyBadGatewayException(String message) {
    super(message);
  }
}

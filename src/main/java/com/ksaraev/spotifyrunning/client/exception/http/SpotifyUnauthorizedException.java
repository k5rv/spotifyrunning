package com.ksaraev.spotifyrunning.client.exception.http;

public class SpotifyUnauthorizedException extends RuntimeException {
  public SpotifyUnauthorizedException(String message) {
    super(message);
  }
}

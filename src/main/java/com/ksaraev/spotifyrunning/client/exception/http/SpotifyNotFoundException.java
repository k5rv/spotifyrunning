package com.ksaraev.spotifyrunning.client.exception.http;

public class SpotifyNotFoundException extends RuntimeException {

  public SpotifyNotFoundException(String message) {
    super(message);
  }
}

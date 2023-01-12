package com.ksaraev.spotifyrunning.client.exception.http;

public class SpotifyInternalServerError extends RuntimeException {

  public SpotifyInternalServerError(String message) {
    super(message);
  }
}

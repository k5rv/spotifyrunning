package com.ksaraev.spotifyrun.client.exception.http;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyNotFoundException extends SpotifyException {
  public static final String SPOTIFY_USER_PROFILE_IS_NULL = "Spotify User Profile is null";
}

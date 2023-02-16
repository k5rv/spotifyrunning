package com.ksaraev.spotifyrun.exception;

import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import lombok.experimental.StandardException;

@StandardException
public class GetUserException extends ApplicationException {
  public static final String UNABLE_TO_GET_USER = "Unable to get user";
  public static final String SPOTIFY_CLIENT_RETURNED_NULL =
      "Spotify client returned null, expected instance of "
          + SpotifyUserProfileItem.class.getSimpleName();
}

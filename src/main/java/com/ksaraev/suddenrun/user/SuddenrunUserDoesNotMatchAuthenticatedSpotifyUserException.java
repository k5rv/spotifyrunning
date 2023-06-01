package com.ksaraev.suddenrun.user;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunUserDoesNotMatchAuthenticatedSpotifyUserException extends RuntimeException {

  public SuddenrunUserDoesNotMatchAuthenticatedSpotifyUserException(String userId) {
    super("User with id" + " [" + userId + "] does not match authenticated Spotify user");
  }
}

package com.ksaraev.suddenrun.user;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunUserDoesNotMatchCurrentSpotifyUserException extends RuntimeException {

  public SuddenrunUserDoesNotMatchCurrentSpotifyUserException(String userId) {
    super("User with id" + " [" + userId + "] does not match current Spotify user");
  }
}

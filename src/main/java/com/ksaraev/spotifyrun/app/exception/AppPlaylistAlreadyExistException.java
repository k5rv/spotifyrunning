package com.ksaraev.spotifyrun.app.exception;

public class AppPlaylistAlreadyExistException extends RuntimeException {

  public AppPlaylistAlreadyExistException(String appUserId, String appPlaylistId) {
    super("User with id [" + appUserId + "] already has playlist [" + appPlaylistId + "]");
  }
}

package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class GetAddedTracksException extends RuntimeException {

  private static final String ERROR_WHILE_REVISING_CUSTOM_TRACKS_FOR_USER_WITH_ID =
      "Error while getting added tracks by user with id";

  public GetAddedTracksException(
      String userId, String playlistId, Throwable cause) {
    super(
        ERROR_WHILE_REVISING_CUSTOM_TRACKS_FOR_USER_WITH_ID
            + " ["
            + userId
            + "]"
            + " and playlist id: ["
            + playlistId
            + "]"
            + " :"
            + cause.getMessage());
  }
}

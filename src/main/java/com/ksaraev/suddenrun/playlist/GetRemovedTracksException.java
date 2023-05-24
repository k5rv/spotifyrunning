package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class GetRemovedTracksException extends RuntimeException {

  private static final String ERROR_WHILE_REVISING_REJECTED_TRACKS_FOR_USER_WITH_ID =
      "Error while getting removed tracks by user with id";

  public GetRemovedTracksException(
      String userId, String playlistId, Throwable cause) {
    super(
        ERROR_WHILE_REVISING_REJECTED_TRACKS_FOR_USER_WITH_ID
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

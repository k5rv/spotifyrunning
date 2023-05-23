package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunAddPlaylistTracksException extends RuntimeException {

  private static final String ERROR_WHILE_ADDING_TRACKS_TO_PLAYLIST_WITH_ID =
      "Error while adding tracks to playlist with id [";

  public SuddenrunAddPlaylistTracksException(String playlistId, Throwable cause) {
    super(
        ERROR_WHILE_ADDING_TRACKS_TO_PLAYLIST_WITH_ID + playlistId + "]: " + cause.getMessage(),
        cause);
  }
}

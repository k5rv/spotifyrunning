package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException

public class PlaylistExistenceException extends RuntimeException {

    public static final String UNABLE_TO_GET_PLAYLIST_STATUS = "Unable to get playlist status: ";
}

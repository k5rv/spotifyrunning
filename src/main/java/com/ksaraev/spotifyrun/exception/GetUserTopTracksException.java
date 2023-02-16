package com.ksaraev.spotifyrun.exception;

import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import lombok.experimental.StandardException;

import java.util.Arrays;

@StandardException
public class GetUserTopTracksException extends ApplicationException {
  public static final String UNABLE_TO_GET_USER_TOP_TRACKS = "Unable to get user top tracks";
  public static final String SPOTIFY_CLIENT_RETURNED_NULL =
      "Spotify client returned null, expected instance of "
          + GetUserTopTracksResponse.class.getSimpleName();

  public static final String CONFIGURATION_ERROR_NOT_VALID_TIME_RANGE_PARAMETER =
      "Configuration error, time range parameter value is [%s]. Expected values are "
          + Arrays.asList(GetUserTopTracksRequest.TimeRange.values());
}

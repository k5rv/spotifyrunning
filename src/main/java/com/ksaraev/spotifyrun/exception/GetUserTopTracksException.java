package com.ksaraev.spotifyrun.exception;

import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import lombok.experimental.StandardException;

import java.util.Arrays;

@StandardException
public class GetUserTopTracksException extends ApplicationException {
  public static final String UNABLE_TO_GET_USER_TOP_TRACKS = "Unable to get user top tracks: ";

  public static final String ILLEGAL_TIME_RANGE =
      "Expected time range parameter values are "
          + Arrays.asList(GetUserTopTracksRequest.TimeRange.values())
          + " actual is ";
}

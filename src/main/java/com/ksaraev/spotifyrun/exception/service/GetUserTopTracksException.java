package com.ksaraev.spotifyrun.exception.service;

import com.ksaraev.spotifyrun.client.api.GetUserTopTracksRequest;
import java.util.Arrays;
import lombok.experimental.StandardException;

@StandardException
public class GetUserTopTracksException extends RuntimeException {
  public static final String UNABLE_TO_GET_USER_TOP_TRACKS = "Unable to get user top tracks: ";

  public static final String ILLEGAL_TIME_RANGE =
      "Expected time range parameter values are "
          + Arrays.asList(GetUserTopTracksRequest.TimeRange.values())
          + " actual is ";
}

package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.GetUserTopTracksException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

import static com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest.TimeRange;
import static com.ksaraev.spotifyrun.exception.GetUserTopTracksException.*;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserTopTracksService implements SpotifyUserTopTracksService {

  private final SpotifyClient spotifyClient;
  private final SpotifyGetUserTopTracksRequestConfig requestConfig;
  private final TrackMapper trackMapper;

  @Override
  public List<@Valid SpotifyTrack> getUserTopTracks() {
    if (Arrays.stream(TimeRange.values())
        .noneMatch(timeRange -> timeRange.name().equals(requestConfig.getTimeRange()))) {
      throw new GetUserTopTracksException(
          UNABLE_TO_GET_USER_TOP_TRACKS
              + ": "
              + CONFIGURATION_ERROR_NOT_VALID_TIME_RANGE_PARAMETER.formatted(
                  requestConfig.getTimeRange()));
    }
    GetUserTopTracksResponse response;
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(TimeRange.valueOf(requestConfig.getTimeRange()))
              .limit(requestConfig.getLimit())
              .build();
      response = spotifyClient.getUserTopTracks(request);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + ": " + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetUserTopTracksException(UNABLE_TO_GET_USER_TOP_TRACKS + ": " + e.getMessage(), e);
    }
    if (response == null) {
      throw new GetUserTopTracksException(
          UNABLE_TO_GET_USER_TOP_TRACKS + ": " + SPOTIFY_CLIENT_RETURNED_NULL);
    }
    try {
      return trackMapper.mapItemsToTracks(response.trackItems());
    } catch (RuntimeException e) {
      throw new GetUserTopTracksException(UNABLE_TO_GET_USER_TOP_TRACKS + ": " + e.getMessage());
    }
  }
}

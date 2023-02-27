package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyForbiddenException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyTooManyRequestsException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException;
import com.ksaraev.spotifyrun.exception.spotify.ForbiddenException;
import com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException;
import com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest.TimeRange;
import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.ILLEGAL_TIME_RANGE;
import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;
import static com.ksaraev.spotifyrun.exception.spotify.ForbiddenException.FORBIDDEN;
import static com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException.TOO_MANY_REQUESTS;
import static com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException.UNAUTHORIZED;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTopTracksService implements SpotifyUserTopTracksService {

  private final SpotifyClient spotifyClient;
  private final SpotifyGetUserTopTracksRequestConfig requestConfig;
  private final TrackMapper trackMapper;

  @Override
  public List<SpotifyTrack> getUserTopTracks() {
    if (Arrays.stream(TimeRange.values())
        .noneMatch(tr -> tr.name().equals(requestConfig.getTimeRange()))) {
      throw new GetUserTopTracksException(
          UNABLE_TO_GET_USER_TOP_TRACKS + ILLEGAL_TIME_RANGE + requestConfig.getTimeRange());
    }
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(TimeRange.valueOf(requestConfig.getTimeRange()))
              .limit(requestConfig.getLimit())
              .build();
      GetUserTopTracksResponse response = spotifyClient.getUserTopTracks(request);
      if (CollectionUtils.isEmpty(response.trackItems())) return List.of();
      List<SpotifyTrackItem> trackItems = response.trackItems();
      trackItems.removeAll(Collections.singleton(null));
      return trackMapper.mapItemsToTracks(trackItems);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + e.getMessage(), e);
    } catch (SpotifyForbiddenException e) {
      throw new ForbiddenException(FORBIDDEN + e.getMessage(), e);
    } catch (SpotifyTooManyRequestsException e) {
      throw new TooManyRequestsException(TOO_MANY_REQUESTS + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetUserTopTracksException(UNABLE_TO_GET_USER_TOP_TRACKS + e.getMessage(), e);
    }
  }
}

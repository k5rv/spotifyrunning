package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest.TimeRange;
import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.ILLEGAL_TIME_RANGE;
import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;

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
        .noneMatch(timeRange -> timeRange.name().equals(requestConfig.getTimeRange()))) {
      throw new GetUserTopTracksException(
          UNABLE_TO_GET_USER_TOP_TRACKS + ILLEGAL_TIME_RANGE + requestConfig.getTimeRange());
    }
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(TimeRange.valueOf(requestConfig.getTimeRange()))
              .limit(requestConfig.getLimit())
              .offset(requestConfig.getOffset())
              .build();
      GetUserTopTracksResponse response = spotifyClient.getUserTopTracks(request);
      if (CollectionUtils.isEmpty(response.trackItems())) return List.of();
      List<SpotifyTrackItem> trackItems =
          response.trackItems().stream().filter(Objects::nonNull).toList();
      if (trackItems.isEmpty()) return List.of();
      return trackMapper.mapItemsToTracks(trackItems);
    } catch (RuntimeException e) {
      throw new GetUserTopTracksException(UNABLE_TO_GET_USER_TOP_TRACKS + e.getMessage(), e);
    }
  }
}

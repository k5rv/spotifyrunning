package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.client.dto.GetUserTopTracksRequest.TimeRange;
import static com.ksaraev.spotifyrun.exception.business.GetUserTopTracksException.ILLEGAL_TIME_RANGE;
import static com.ksaraev.spotifyrun.exception.business.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.dto.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.client.dto.SpotifyTrackDto;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.business.GetUserTopTracksException;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyUserTopTracksService implements SpotifyUserTopTrackItemsService {

  private final SpotifyClient spotifyClient;
  private final SpotifyGetUserTopTracksRequestConfig getUserTopTracksRequestConfig;
  private final SpotifyTrackMapper trackMapper;

  @Override
  public List<SpotifyTrackItem> getUserTopTracks() {
    if (Arrays.stream(TimeRange.values())
        .noneMatch(
            timeRange -> timeRange.name().equals(getUserTopTracksRequestConfig.getTimeRange()))) {
      throw new GetUserTopTracksException(
          UNABLE_TO_GET_USER_TOP_TRACKS
              + ILLEGAL_TIME_RANGE
              + getUserTopTracksRequestConfig.getTimeRange());
    }
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(TimeRange.valueOf(getUserTopTracksRequestConfig.getTimeRange()))
              .limit(getUserTopTracksRequestConfig.getLimit())
              .offset(getUserTopTracksRequestConfig.getOffset())
              .build();

      GetUserTopTracksResponse response = spotifyClient.getUserTopTracks(request);

      List<SpotifyTrackDto> trackItems =
          response.trackItems().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return trackItems.isEmpty() ? List.of() : trackMapper.mapItemsToTracks(trackItems);
    } catch (RuntimeException e) {
      throw new GetUserTopTracksException(UNABLE_TO_GET_USER_TOP_TRACKS + e.getMessage(), e);
    }
  }
}

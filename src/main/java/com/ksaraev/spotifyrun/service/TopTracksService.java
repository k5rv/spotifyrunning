package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.TopTracksServiceException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class TopTracksService implements SpotifyTopTracksService {

  private final SpotifyClient spotifyClient;
  private final SpotifyGetUserTopTracksRequestConfig requestConfig;
  private final TrackMapper trackMapper;

  @Override
  public List<@Valid SpotifyTrack> getTopTracks() {
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(GetUserTopTracksRequest.TimeRange.valueOf(requestConfig.getTimeRange()))
              .limit(requestConfig.getLimit())
              .build();

      GetUserTopTracksResponse response = spotifyClient.getUserTopTracks(request);

      List<SpotifyTrack> tracks =
          response.trackItems().stream()
              .filter(Objects::nonNull)
              .map(trackMapper::toModel)
              .map(SpotifyTrack.class::cast)
              .toList();

      if (tracks.isEmpty()) {
        return List.of();
      }

      return tracks;
    } catch (RuntimeException e) {
      throw new TopTracksServiceException("Error while getting top tracks", e);
    }
  }
}

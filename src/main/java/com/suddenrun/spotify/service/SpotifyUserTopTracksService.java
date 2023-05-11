package com.suddenrun.spotify.service;

import com.suddenrun.client.SpotifyClient;
import com.suddenrun.client.dto.GetUserTopTracksRequest;
import com.suddenrun.client.dto.GetUserTopTracksResponse;
import com.suddenrun.client.dto.SpotifyTrackDto;
import com.suddenrun.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.GetSpotifyUserTopItemsRequestConfig;
import com.suddenrun.spotify.exception.SpotifyServiceAuthenticationException;
import com.suddenrun.spotify.exception.SpotifyUserTopTracksServiceException;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
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

  private final GetSpotifyUserTopItemsRequestConfig getUserTopTracksRequestConfig;

  private final SpotifyTrackMapper trackMapper;

  @Override
  public List<SpotifyTrackItem> getUserTopTracks() {
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(GetUserTopTracksRequest.TimeRange.valueOf(getUserTopTracksRequestConfig.getTimeRange()))
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
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyUserTopTracksServiceException(e);
    }
  }
}

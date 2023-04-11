package com.ksaraev.spotifyrun.spotify.service;

import static com.ksaraev.spotifyrun.client.dto.GetUserTopTracksRequest.TimeRange;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.dto.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.client.dto.SpotifyTrackDto;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.spotify.config.GetSpotifyUserTopItemsRequestConfig;
import com.ksaraev.spotifyrun.spotify.exception.SpotifyServiceAuthenticationException;
import com.ksaraev.spotifyrun.spotify.exception.SpotifyUserTopTracksServiceException;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackMapper;
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
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyUserTopTracksServiceException(e);
    }
  }
}

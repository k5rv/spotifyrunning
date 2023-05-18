package com.suddenrun.spotify.service;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.GetUserTopTracksRequest;
import com.suddenrun.spotify.client.dto.GetUserTopTracksResponse;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.GetSpotifyUserTopItemsRequestConfig;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.exception.GetSpotifyUserTopTracksException;
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

  private final SpotifyClient client;

  private final GetSpotifyUserTopItemsRequestConfig requestConfig;

  private final SpotifyTrackMapper mapper;

  @Override
  public List<SpotifyTrackItem> getUserTopTracks() {
    try {
      GetUserTopTracksRequest request =
          GetUserTopTracksRequest.builder()
              .timeRange(GetUserTopTracksRequest.TimeRange.valueOf(requestConfig.getTimeRange()))
              .limit(requestConfig.getLimit())
              .offset(requestConfig.getOffset())
              .build();

      GetUserTopTracksResponse response = client.getUserTopTracks(request);

      List<SpotifyTrackDto> trackDtos =
          response.trackDtos().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return trackDtos.isEmpty() ? List.of() : mapper.mapDtosToModels(trackDtos);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyUserTopTracksException(e);
    }
  }
}

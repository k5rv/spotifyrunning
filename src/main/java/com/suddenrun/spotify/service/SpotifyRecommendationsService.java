package com.suddenrun.spotify.service;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.GetRecommendationsRequest;
import com.suddenrun.spotify.client.dto.GetRecommendationsResponse;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.GetSpotifyRecommendationItemsRequestConfig;
import com.suddenrun.spotify.exception.GetSpotifyRecommendationsException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackFeaturesMapper;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SpotifyRecommendationsService implements SpotifyRecommendationItemsService {

  private final SpotifyClient client;

  private final GetSpotifyRecommendationItemsRequestConfig requestConfig;

  private final SpotifyTrackMapper trackMapper;

  private final SpotifyTrackFeaturesMapper featuresMapper;

  @Override
  public List<SpotifyTrackItem> getRecommendations(
      @NotNull List<SpotifyTrackItem> seedTrackItems, @NotNull SpotifyTrackItemFeatures trackItemFeatures) {
    try {
      List<String> seedTrackIds = seedTrackItems.stream().map(SpotifyTrackItem::getId).toList();

      GetRecommendationsRequest request =
          GetRecommendationsRequest.builder()
              .seedTrackIds(seedTrackIds)
              .trackFeatures(featuresMapper.mapToRequestFeatures(trackItemFeatures))
              .limit(requestConfig.getLimit())
              .build();

      GetRecommendationsResponse response = client.getRecommendations(request);

      List<SpotifyTrackDto> trackDtos =
          response.trackDtos().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return trackDtos.isEmpty() ? List.of() : trackMapper.mapDtosToModels(trackDtos);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyRecommendationsException(e);
    }
  }
}

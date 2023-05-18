package com.suddenrun.spotify.service;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.GetRecommendationsRequest;
import com.suddenrun.spotify.client.dto.GetRecommendationsResponse;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.GetSpotifyRecommendationItemsRequestConfig;
import com.suddenrun.spotify.exception.SpotifyRecommendationsServiceException;
import com.suddenrun.spotify.exception.SpotifyServiceAuthenticationException;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackFeaturesMapper;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SpotifyRecommendationsService implements SpotifyRecommendationItemsService {

  private final SpotifyClient spotifyClient;

  private final GetSpotifyRecommendationItemsRequestConfig getRecommendationsRequestConfig;

  private final SpotifyTrackMapper trackMapper;

  private final SpotifyTrackFeaturesMapper trackFeaturesMapper;

  @Override
  public List<SpotifyTrackItem> getRecommendations(
      List<SpotifyTrackItem> seedTracks, SpotifyTrackItemFeatures trackFeatures) {
    try {
      List<String> seedTrackIds = seedTracks.stream().map(SpotifyTrackItem::getId).toList();

      GetRecommendationsRequest request =
          GetRecommendationsRequest.builder()
              .seedTrackIds(seedTrackIds)
              .trackFeatures(trackFeaturesMapper.mapToRequestFeatures(trackFeatures))
              .limit(getRecommendationsRequestConfig.getLimit())
              .build();

      GetRecommendationsResponse response = spotifyClient.getRecommendations(request);

      List<SpotifyTrackDto> trackItems =
          response.trackItems().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return trackItems.isEmpty() ? List.of() : trackMapper.mapItemsToTracks(trackItems);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyRecommendationsServiceException(e);
    }
  }
}

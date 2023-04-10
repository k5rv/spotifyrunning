package com.ksaraev.spotifyrun.spotify.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.dto.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.dto.SpotifyTrackDto;
import com.ksaraev.spotifyrun.spotify.config.GetSpotifyRecommendationItemsRequestConfig;
import com.ksaraev.spotifyrun.spotify.exception.refactored.GetSpotifyRecommendationsException;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackMapper;
import com.ksaraev.spotifyrun.spotify.model.trackfeatures.SpotifyTrackFeaturesMapper;
import com.ksaraev.spotifyrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
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
    } catch (RuntimeException e) {
      throw new GetSpotifyRecommendationsException(e);
    }
  }
}

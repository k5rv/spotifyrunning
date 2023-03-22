package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.GetRecommendationsException.UNABLE_TO_GET_RECOMMENDATIONS;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.api.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.api.SpotifyTrackDto;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.exception.business.GetRecommendationsException;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackMapper;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackFeaturesMapper;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackItemFeatures;
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
  private final SpotifyGetRecommendationsRequestConfig getRecommendationsRequestConfig;
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
      throw new GetRecommendationsException(UNABLE_TO_GET_RECOMMENDATIONS + e.getMessage(), e);
    }
  }

  @Override
  public List<SpotifyTrackItem> getRecommendations(List<SpotifyTrackItem> seedTracks) {
    return getRecommendations(seedTracks, SpotifyTrackFeatures.builder().build());
  }
}

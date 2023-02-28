package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyForbiddenException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyTooManyRequestsException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetRecommendationsException;
import com.ksaraev.spotifyrun.exception.spotify.ForbiddenException;
import com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException;
import com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.track.TrackFeaturesMapper;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static com.ksaraev.spotifyrun.exception.service.GetRecommendationsException.UNABLE_TO_GET_RECOMMENDATIONS;
import static com.ksaraev.spotifyrun.exception.spotify.ForbiddenException.FORBIDDEN;
import static com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException.TOO_MANY_REQUESTS;
import static com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException.UNAUTHORIZED;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class RecommendationsService implements SpotifyRecommendationsService {
  private final SpotifyClient spotifyClient;
  private final SpotifyGetRecommendationsRequestConfig requestConfig;
  private final TrackMapper trackMapper;
  private final TrackFeaturesMapper trackFeaturesMapper;

  @Override
  public List<SpotifyTrack> getRecommendations(
      List<SpotifyTrack> seedTracks, SpotifyTrackFeatures trackFeatures) {
    try {
      List<String> seedTrackIds = seedTracks.stream().map(SpotifyTrack::getId).toList();
      GetRecommendationsRequest request =
          GetRecommendationsRequest.builder()
              .seedTrackIds(seedTrackIds)
              .trackFeatures(trackFeaturesMapper.mapToRequestFeatures(trackFeatures))
              .limit(requestConfig.getLimit())
              .build();
      GetRecommendationsResponse response = spotifyClient.getRecommendations(request);
      if (CollectionUtils.isEmpty(response.trackItems())) return List.of();
      List<SpotifyTrackItem> trackItems = response.trackItems();
      trackItems.removeAll(Collections.singleton(null));
      return trackMapper.mapItemsToTracks(trackItems);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + e.getMessage(), e);
    } catch (SpotifyForbiddenException e) {
      throw new ForbiddenException(FORBIDDEN + e.getMessage(), e);
    } catch (SpotifyTooManyRequestsException e) {
      throw new TooManyRequestsException(TOO_MANY_REQUESTS + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetRecommendationsException(UNABLE_TO_GET_RECOMMENDATIONS + e.getMessage(), e);
    }
  }
}

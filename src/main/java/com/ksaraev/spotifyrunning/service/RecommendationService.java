package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetRecommendationItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.config.requests.SpotifyRequestConfig;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RecommendationService implements SpotifyRecommendationService {
  private final SpotifyClient spotifyClient;

  private final SpotifyRequestConfig spotifyRequestConfig;
  private final TrackMapper trackMapper;

  @Override
  public List<SpotifyTrack> getTracks(
      List<SpotifyTrack> seedTracks,
      List<SpotifyArtist> seedArtists,
      List<String> seedGenres,
      SpotifyRecommendationFeatures recommendationFeatures) {
    log.info(
        "Getting tracks with seed: {}, {}, {} and features: {}",
        seedTracks,
        seedArtists,
        seedGenres,
        recommendationFeatures);

    GetSpotifyUserItemsRequest request =
        GetRecommendationItemsRequest.builder()
            .seedTracks(seedTracks.stream().map(SpotifyTrack::getId).toList())
            .seedArtists(seedArtists.stream().map(SpotifyArtist::getId).toList())
            .seedGenres(seedGenres)
            .spotifyRecommendationFeatures(recommendationFeatures)
            .limit(spotifyRequestConfig.getRecommendationItemsRequestLimit())
            .build();

    SpotifyItemsResponse response = spotifyClient.getRecommendations(request);

    if (Objects.isNull(response)) {
      throw new IllegalStateException("Tracks response is null");
    }

    List<SpotifyTrack> tracksRecommendation =
        response.getItems().stream()
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .map(SpotifyTrack.class::cast)
            .toList();

    log.info("Tracks received: {}", tracksRecommendation);
    return tracksRecommendation;
  }
}

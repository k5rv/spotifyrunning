package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetRecommendationItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.config.recommendations.SpotifyRecommendationsConfig;
import com.ksaraev.spotifyrunning.exception.InvalidRecommendationSeedException;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RecommendationsService implements SpotifyRecommendationsService {
  private final SpotifyClient spotifyClient;

  private final SpotifyRecommendationsConfig spotifyRecommendationsConfig;
  private final TrackMapper trackMapper;

  @Override
  public List<SpotifyTrack> getTracks(
      @NotNull List<SpotifyTrack> seedTracks,
      @NotNull List<SpotifyArtist> seedArtists,
      @NotNull List<String> seedGenres,
      SpotifyRecommendationsFeatures recommendationsFeatures) {

    int seedSize =
        Stream.of(seedTracks, seedArtists, seedGenres)
            .filter(Objects::nonNull)
            .mapToInt(List::size)
            .sum();

    if (seedSize <= 0 || seedSize > 5) {
      throw new InvalidRecommendationSeedException(
          "Expected seed size must be in [1...5] range. Actual seed size is %s. Seed sizes: tracks: %s, artists: %s, genres: %s."
              .formatted(seedSize, seedTracks.size(), seedArtists.size(), seedGenres.size()));
    }

    GetRecommendationItemsRequest request =
        GetRecommendationItemsRequest.builder()
            .seedTracks(seedTracks.stream().map(SpotifyEntity::getId).toList())
            .seedArtists(seedArtists.stream().map(SpotifyEntity::getId).toList())
            .seedGenres(seedGenres)
            .spotifyRecommendationsFeatures(recommendationsFeatures)
            .limit(spotifyRecommendationsConfig.getRecommendationItemsRequestLimit())
            .build();

    SpotifyItemsResponse response = spotifyClient.getRecommendations(request);

    List<SpotifyTrack> tracksRecommendations =
        response.getItems().stream()
            .filter(Objects::nonNull)
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .map(SpotifyTrack.class::cast)
            .toList();

    if (tracksRecommendations.isEmpty()) {
      return List.of();
    }

    return tracksRecommendations;
  }
}

package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrunning.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrunning.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrunning.config.recommendations.SpotifyRecommendationsConfig;
import com.ksaraev.spotifyrunning.exception.InvalidRecommendationSeedException;
import com.ksaraev.spotifyrunning.model.recommendations.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyEntity;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
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
          "Expected seed size must be in [1...5] range. Actual seed size is %s. Seed sizes: tracks: %s, spotifyArtistItems: %s, genres: %s."
              .formatted(seedSize, seedTracks.size(), seedArtists.size(), seedGenres.size()));
    }

    GetRecommendationsRequest request =
        GetRecommendationsRequest.builder()
            .seedTracks(seedTracks.stream().map(SpotifyEntity::getId).toList())
            .seedArtists(seedArtists.stream().map(SpotifyEntity::getId).toList())
            .seedGenres(seedGenres)
            .spotifyRecommendationsFeatures(recommendationsFeatures)
            .limit(spotifyRecommendationsConfig.getRecommendationItemsRequestLimit())
            .build();

    GetRecommendationsResponse response = spotifyClient.getRecommendations(request);

    List<SpotifyTrack> tracksRecommendations =
        response.spotifyTrackItems().stream()
            .filter(Objects::nonNull)
            .map(SpotifyTrackItem.class::cast)
            .map(trackMapper::toModel)
            .map(SpotifyTrack.class::cast)
            .toList();

    if (tracksRecommendations.isEmpty()) {
      return List.of();
    }

    return tracksRecommendations;
  }
}

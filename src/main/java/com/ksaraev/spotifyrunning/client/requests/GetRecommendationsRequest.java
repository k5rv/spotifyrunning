package com.ksaraev.spotifyrunning.client.requests;

import com.ksaraev.spotifyrunning.model.recommendations.SpotifyRecommendationsFeatures;
import lombok.Builder;

import java.util.List;

public record GetRecommendationsRequest(
    List<String> seedArtists,
    List<String> seedTracks,
    List<String> seedGenres,
    SpotifyRecommendationsFeatures spotifyRecommendationsFeatures,
    Integer limit,
    Integer offset) {
  @Builder
  public GetRecommendationsRequest {}
}

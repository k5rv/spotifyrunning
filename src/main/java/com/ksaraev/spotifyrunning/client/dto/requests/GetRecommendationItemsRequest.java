package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
public class GetRecommendationItemsRequest implements GetSpotifyUserItemsRequest {

  @NotNull List<String> seedArtists;

  @NotNull List<String> seedTracks;

  @NotNull List<String> seedGenres;

  @Min(0)
  @Max(50)
  Integer limit;

  @Min(0)
  Integer offset;

  SpotifyRecommendationFeatures spotifyRecommendationFeatures;
}

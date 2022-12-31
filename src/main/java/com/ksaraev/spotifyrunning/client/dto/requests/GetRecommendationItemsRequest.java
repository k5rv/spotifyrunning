package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Value
@Builder
public class GetRecommendationItemsRequest implements GetSpotifyUserItemsRequest {

  List<String> seedArtists;

  List<String> seedTracks;

  List<String> seedGenres;

  SpotifyRecommendationsFeatures spotifyRecommendationsFeatures;

  @Min(0)
  @Max(50)
  Integer limit;

  @Min(0)
  Integer offset;
}

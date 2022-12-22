package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import feign.Param;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Value
@Builder
public class GetRecommendationItemsRequest implements GetSpotifyUserItemsRequest {

  @NotNull
  @Size(min = 1, max = 5)
  @Param("seed_artists")
  List<String> seedArtists;

  @NotNull
  @Size(min = 1, max = 5)
  @Param("seed_tracks")
  List<String> seedTracks;

  @NotNull
  @Size(min = 1, max = 5)
  @Param("seed_genres")
  List<String> seedGenres;

  @Min(0)
  @Max(50)
  Integer limit;

  @Min(0)
  Integer offset;

  SpotifyRecommendationFeatures spotifyRecommendationFeatures;
}

package com.ksaraev.spotifyrun.spotify.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetSpotifyRecommendationsRequestConfig implements GetSpotifyRecommendationItemsRequestConfig {

  private Integer limit;
}

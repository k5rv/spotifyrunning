package com.ksaraev.spotify.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSpotifyRecommendationsRequestConfig
    implements GetSpotifyRecommendationItemsRequestConfig {

  private Integer limit;
}

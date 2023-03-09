package com.ksaraev.spotifyrun.config.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRecommendationsRequestConfig implements SpotifyGetRecommendationsRequestConfig {

  private Integer limit;
}

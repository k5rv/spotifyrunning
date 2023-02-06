package com.ksaraev.spotifyrun.config.requests;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GetRecommendationsRequestConfig implements SpotifyGetRecommendationsRequestConfig {

  @Value("${spotifyrun.config.client.requests.get-recommendations.limit}")
  private Integer limit;
}

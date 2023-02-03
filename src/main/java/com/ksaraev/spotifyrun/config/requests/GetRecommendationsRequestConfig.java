package com.ksaraev.spotifyrun.config.requests;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GetRecommendationsRequestConfig implements SpotifyGetRecommendationsRequestConfig {

  @Value("${spotify-run.config.client.requests.get-recommendations.limit}")
  private Integer limit;
}

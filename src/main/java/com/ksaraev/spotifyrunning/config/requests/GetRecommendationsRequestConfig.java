package com.ksaraev.spotifyrunning.config.requests;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GetRecommendationsRequestConfig implements SpotifyGetRecommendationsRequestConfig {

  @Value("${app.requests.get-recommendations.limit}")
  private Integer limit;
}

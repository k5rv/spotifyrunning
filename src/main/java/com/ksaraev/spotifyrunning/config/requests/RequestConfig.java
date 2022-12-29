package com.ksaraev.spotifyrunning.config.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
public class RequestConfig implements SpotifyRequestConfig {

  @Value("${app.requests.get-user-top-items.limit}")
  private Integer userTopItemsRequestLimit;

  @Value("${app.requests.get-recommendations.limit}")
  private Integer recommendationItemsRequestLimit;

  @Value("${app.requests.get-recommendations.time-range}")
  private String userTopItemsRequestTimeRange;

  @Override
  public Integer getUserTopItemsRequestLimit() {
    return this.userTopItemsRequestLimit;
  }

  @Override
  public Integer getRecommendationItemsRequestLimit() {
    return this.recommendationItemsRequestLimit;
  }

  @Override
  public String getUserTopItemsRequestTimeRange() {
    return this.userTopItemsRequestTimeRange;
  }
}

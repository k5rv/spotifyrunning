package com.ksaraev.spotifyrunning.config.topitems;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
public class UserTopItemsConfig implements SpotifyUserTopItemsConfig {

  @Value("${app.requests.get-user-top-items.limit}")
  private Integer userTopItemsRequestLimit;

  @Value("${app.requests.get-recommendations.time-range}")
  private String userTopItemsRequestTimeRange;

  @Override
  public Integer getUserTopItemsRequestLimit() {
    return this.userTopItemsRequestLimit;
  }

  @Override
  public String getUserTopItemsRequestTimeRange() {
    return this.userTopItemsRequestTimeRange;
  }
}

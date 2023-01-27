package com.ksaraev.spotifyrunning.config.requests;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GetUserTopTracksRequestConfig implements SpotifyGetUserTopTracksRequestConfig {

  @Value("${app.requests.get-user-top-items.limit}")
  private Integer limit;

  @Value("${app.requests.get-recommendations.time-range}")
  private String timeRange;
}

package com.ksaraev.spotifyrunning.config.requests;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GetUserTopTracksRequestConfig implements SpotifyGetUserTopTracksRequestConfig {

  @Value("${spotify-running.config.client.requests.get-user-top-tracks.limit}")
  private Integer limit;

  @Value("${spotify-running.config.client.requests.get-user-top-tracks.time-range}")
  private String timeRange;
}

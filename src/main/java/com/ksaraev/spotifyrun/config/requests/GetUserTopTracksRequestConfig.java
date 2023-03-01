package com.ksaraev.spotifyrun.config.requests;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GetUserTopTracksRequestConfig implements SpotifyGetUserTopTracksRequestConfig {
  @Value("${spotifyrun.config.client.requests.get-user-top-tracks.limit}")
  private Integer limit;

  @Value("${spotifyrun.config.client.requests.get-user-top-tracks.offset}")
  private Integer offset;

  @Value("${spotifyrun.config.client.requests.get-user-top-tracks.time-range}")
  private String timeRange;
}

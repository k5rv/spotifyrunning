package com.ksaraev.spotifyrun.config.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserTopTracksRequestConfig implements SpotifyGetUserTopTracksRequestConfig {
  private Integer limit;

  private Integer offset;

  private String timeRange;
}

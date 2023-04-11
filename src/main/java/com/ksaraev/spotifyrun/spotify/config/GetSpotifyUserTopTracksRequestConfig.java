package com.ksaraev.spotifyrun.spotify.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSpotifyUserTopTracksRequestConfig implements GetSpotifyUserTopItemsRequestConfig {
  private Integer limit;

  private Integer offset;

  private String timeRange;
}

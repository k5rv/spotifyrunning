package com.ksaraev.spotifyrun.config.requests;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
public class GetUserTopTracksRequestConfig implements SpotifyGetUserTopTracksRequestConfig {
  private Integer limit;

  private Integer offset;

  private String timeRange;
}

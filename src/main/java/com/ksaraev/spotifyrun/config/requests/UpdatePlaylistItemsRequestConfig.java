package com.ksaraev.spotifyrun.config.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlaylistItemsRequestConfig implements SpotifyUpdatePlaylistItemsRequestConfig {
  private Integer position;
}

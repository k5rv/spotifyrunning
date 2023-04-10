package com.ksaraev.spotifyrun.spotify.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddSpotifyPlaylistTracksRequestConfig implements AddSpotifyPlaylistItemsRequestConfig {
  private Integer position;
}

package com.ksaraev.spotifyrun.spotify.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddSpotifyPlaylistTracksRequestConfig implements AddSpotifyPlaylistItemsRequestConfig {
  private Integer position;
}

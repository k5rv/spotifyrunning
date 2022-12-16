package com.ksaraev.spotifyrunning.client.dto.items;

public interface SpotifyAddableContent {

  String getAddedAt();

  SpotifyItem getAddedBy();

  Boolean getIsLocal();

  SpotifyItem getSpotifyItem();
}

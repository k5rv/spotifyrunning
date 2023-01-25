package com.ksaraev.spotifyrunning.client.dto.items.album;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyIllustrated;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPublished;

import java.util.List;

@JsonDeserialize(as = AlbumItemDTO.class)
public interface SpotifyAlbumItem extends SpotifyItem, SpotifyPublished, SpotifyIllustrated {

  String getAlbumType();

  Integer getTotalTracks();

  String getReleaseDate();

  String getReleaseDatePrecision();

  List<String> getAvailableMarkets();
}

package com.ksaraev.spotifyrunning.client.dto.items.track;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPopularity;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPublished;
import com.ksaraev.spotifyrunning.client.dto.items.album.AlbumItemDTO;
import com.ksaraev.spotifyrunning.client.dto.items.artist.SpotifyArtistDTO;

import java.net.URL;
import java.util.List;
import java.util.Map;

@JsonDeserialize(as = SpotifyTrackDTO.class)
public interface SpotifyTrackItem extends SpotifyItem, SpotifyPublished, SpotifyPopularity {

  AlbumItemDTO getAlbum();

  URL getPreviewUrl();

  Boolean getIsLocal();

  Boolean getIsPlayable();

  Integer getDurationMs();

  Integer getTrackNumber();

  Integer getDiscNumber();

  AlbumItemDTO getLinkedFrom();

  List<SpotifyArtistDTO> getArtists();

  List<String> getAvailableMarkets();

  Map<String, Object> getExternalIds();
}

package com.ksaraev.spotifyrunning.client.dto.items.track;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.album.AlbumItemDTO;
import com.ksaraev.spotifyrunning.client.dto.items.artist.SpotifyArtistDTO;
import lombok.Value;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Value
public class SpotifyTrackDTO implements SpotifyTrackItem {

  @JsonProperty("album")
  AlbumItemDTO album;

  @JsonProperty("preview_url")
  URL previewUrl;

  @JsonProperty("is_local")
  Boolean isLocal;

  @JsonProperty("is_playable")
  Boolean isPlayable;

  @JsonProperty("duration_ms")
  Integer durationMs;

  @JsonProperty("track_number")
  Integer trackNumber;

  @JsonProperty("disc_number")
  Integer discNumber;

  @JsonProperty("linked_from")
  AlbumItemDTO linkedFrom;

  @JsonProperty("artists")
  List<SpotifyArtistDTO> artists;

  @JsonProperty("available_markets")
  List<String> availableMarkets;

  @JsonProperty("external_ids")
  Map<String, Object> externalIds;

  @JsonProperty("external_urls")
  Map<String, Object> externalUrls;

  String id;
  String name;
  Integer popularity;
  String type;
  URI uri;
  URL href;
}

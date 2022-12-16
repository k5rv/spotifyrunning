package com.ksaraev.spotifyrunning.client.dto.items.track;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyNamed;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPopularity;
import com.ksaraev.spotifyrunning.client.dto.items.album.AlbumItem;
import com.ksaraev.spotifyrunning.client.dto.items.artist.ArtistItem;
import lombok.Value;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Value
public class TrackItem implements SpotifyItem, SpotifyNamed, SpotifyPopularity {

  @JsonProperty("album")
  AlbumItem album;

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
  AlbumItem linkedFrom;

  @JsonProperty("artists")
  List<ArtistItem> artists;

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

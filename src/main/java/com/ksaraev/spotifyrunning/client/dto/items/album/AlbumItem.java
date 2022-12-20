package com.ksaraev.spotifyrunning.client.dto.items.album;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyIllustrated;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPublished;
import com.ksaraev.spotifyrunning.client.dto.items.artist.ArtistItem;
import lombok.Value;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Value
public class AlbumItem implements SpotifyItem, SpotifyPublished, SpotifyIllustrated {

  @JsonProperty("album_type")
  String albumType;

  @JsonProperty("total_tracks")
  Integer totalTracks;

  @JsonProperty("release_date")
  String releaseDate;

  @JsonProperty("release_date_precision")
  String releaseDatePrecision;

  @JsonProperty("available_markets")
  List<String> availableMarkets;

  @JsonProperty("external_urls")
  Map<String, Object> externalUrls;

  List<ArtistItem> artists;

  String id;
  String name;
  List<Map<String, Object>> restrictions;
  String type;
  URI uri;
  URL href;
  List<Map<String, Object>> images;
}

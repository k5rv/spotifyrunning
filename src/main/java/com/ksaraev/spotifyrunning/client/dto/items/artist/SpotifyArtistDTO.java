package com.ksaraev.spotifyrunning.client.dto.items.artist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Value
public class SpotifyArtistDTO implements SpotifyArtistItem {

  @JsonProperty("followers")
  Map<String, Object> followers;

  @JsonProperty("external_urls")
  Map<String, Object> externalUrls;

  String id;
  String name;
  Integer popularity;
  List<String> genres;
  String type;
  URI uri;
  URL href;
  List<Map<String, Object>> images;
}

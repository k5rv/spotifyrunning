package com.ksaraev.spotifyrunning.client.dto.items.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Value
public class SpotifyUserProfileDTO implements SpotifyUserProfileItem {

  @JsonProperty("display_name")
  String displayName;

  @JsonProperty("explicit_content")
  Map<String, Object> explicitContent;

  @JsonProperty("followers")
  Map<String, Object> followers;

  @JsonProperty("external_urls")
  Map<String, Object> externalUrls;

  String id;
  String email;
  String country;
  String product;
  String type;
  URI uri;
  URL href;
  List<Map<String, Object>> images;

  @Override
  public String getName() {
    return this.displayName;
  }
}

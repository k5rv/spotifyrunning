package com.ksaraev.spotifyrunning.client.dto.items.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.content.SpotifyPlaylistItemContent;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.SpotifyUserProfileItem;
import lombok.Value;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Value
public class SpotifyPlaylistDTO implements SpotifyPlaylistItem {

  @JsonProperty("snapshot_id")
  String snapshotId;

  @JsonProperty("public")
  Boolean isPublic;

  @JsonProperty("collaborative")
  Boolean isCollaborative;

  @JsonProperty("external_urls")
  Map<String, Object> externalUrls;

  @JsonProperty("followers")
  Map<String, Object> followers;

  @JsonProperty("tracks")
  SpotifyPlaylistItemContent spotifyPlaylistItemContent;

  String id;
  String name;
  String description;
  SpotifyUserProfileItem owner;
  String type;
  URI uri;
  URL href;
  List<Map<String, Object>> images;

}

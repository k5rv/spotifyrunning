package com.ksaraev.spotifyrunning.client.dto.items.playlist.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class SpotifyPlaylistDetailsDTO implements SpotifyPlaylistItemDetails {

  @JsonProperty("collaborative")
  Boolean isCollaborative;

  @JsonProperty("public")
  Boolean isPublic;

  String name;
  String description;
}

package com.ksaraev.spotifyrunning.client.dto.items.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItemDetails;
import lombok.Value;

@Value
public class PlaylistItemDetails implements SpotifyItemDetails {

  @JsonProperty("collaborative")
  Boolean isCollaborative;

  @JsonProperty("public")
  Boolean isPublic;

  String name;
  String description;
}

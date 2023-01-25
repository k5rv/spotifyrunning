package com.ksaraev.spotifyrunning.client.dto.items.playlist.details;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;

@JsonDeserialize(as = SpotifyPlaylistDetailsDTO.class)
public interface SpotifyPlaylistItemDetails {

  @NotNull
  String getName();

  String getDescription();

  Boolean getIsPublic();

  Boolean getIsCollaborative();
}

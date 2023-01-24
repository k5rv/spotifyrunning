package com.ksaraev.spotifyrunning.model.playlist;

import jakarta.validation.constraints.NotNull;

public interface SpotifyPlaylistDetails {
  @NotNull
  String getName();

  void setName(String name);

  String getDescription();

  void setDescription(String description);

  Boolean getIsPublic();

  void setIsPublic(Boolean isPublic);

  Boolean getIsCollaborative();

  void setIsCollaborative(Boolean isCollaborative);
}

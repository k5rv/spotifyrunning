package com.ksaraev.spotifyrun.model.spotify;

public interface SpotifyPlaylistDetails {
  String getName();

  void setName(String name);

  String getDescription();

  void setDescription(String description);

  Boolean getIsPublic();

  void setIsPublic(Boolean isPublic);

  Boolean getIsCollaborative();

  void setIsCollaborative(Boolean isCollaborative);
}

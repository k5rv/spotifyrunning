package com.ksaraev.spotifyrunning.model.spotifyentity;

import jakarta.validation.constraints.NotNull;

import java.net.URI;

public interface SpotifyEntity {

  @NotNull
  String getId();

  void setId(String id);

  String getName();

  void setName(String name);

  @NotNull
  URI getUri();

  void setUri(URI uri);
}

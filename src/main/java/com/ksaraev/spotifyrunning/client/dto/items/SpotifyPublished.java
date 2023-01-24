package com.ksaraev.spotifyrunning.client.dto.items;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public interface SpotifyPublished {

  @NotNull
  String getName();

  @NotNull
  Map<String, Object> getExternalUrls();
}

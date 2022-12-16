package com.ksaraev.spotifyrunning.client.dto.items;

import javax.validation.constraints.NotNull;

public interface SpotifyNamed {

  @NotNull
  String getName();
}

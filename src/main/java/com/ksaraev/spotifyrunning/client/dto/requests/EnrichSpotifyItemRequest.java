package com.ksaraev.spotifyrunning.client.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;

public interface EnrichSpotifyItemRequest {
  @NotNull
  List<URI> getUris();
}

package com.ksaraev.spotifyrunning.client.dto.requests;

import lombok.Builder;
import lombok.Value;

import java.net.URI;
import java.util.List;

@Value
@Builder
public class EnrichItemRequest implements EnrichSpotifyItemRequest {
  List<URI> uris;
}

package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import lombok.Value;

import java.util.List;

@Value
public class ArtistItemsResponse implements SpotifyItemsResponse {
  @JsonProperty("artists")
  List<SpotifyItem> items;

  @JsonCreator
  public ArtistItemsResponse(final List<SpotifyItem> items) {
    this.items = items;
  }
}

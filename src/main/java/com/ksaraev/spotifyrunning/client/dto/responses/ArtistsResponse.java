package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.artist.SpotifyArtistItem;
import lombok.Value;

import java.util.List;

@Value
public class ArtistsResponse implements SpotifyItemsResponse {
  @JsonProperty("artists")
  List<SpotifyArtistItem> items;

  @JsonCreator
  public ArtistsResponse(final List<SpotifyArtistItem> items) {
    this.items = items;
  }
}

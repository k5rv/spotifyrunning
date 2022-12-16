package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class UserRecommendedItemsResponse implements SpotifyItemsResponse {

  @JsonProperty("tracks")
  List<SpotifyItem> items;

  List<Map<String, Object>> seeds;

  @JsonCreator
  public UserRecommendedItemsResponse(
      final List<SpotifyItem> items, final List<Map<String, Object>> seeds) {
    this.items = items;
    this.seeds = seeds;
  }
}

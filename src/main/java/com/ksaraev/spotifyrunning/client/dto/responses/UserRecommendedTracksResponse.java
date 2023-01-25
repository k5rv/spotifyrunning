package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.track.SpotifyTrackItem;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class UserRecommendedTracksResponse implements SpotifyItemsResponse {

  @JsonProperty("tracks")
  List<SpotifyTrackItem> items;

  List<Map<String, Object>> seeds;

  @JsonCreator
  public UserRecommendedTracksResponse(
      final List<SpotifyTrackItem> items, final List<Map<String, Object>> seeds) {
    this.items = items;
    this.seeds = seeds;
  }
}

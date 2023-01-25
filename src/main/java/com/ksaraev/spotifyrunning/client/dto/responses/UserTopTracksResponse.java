package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ksaraev.spotifyrunning.client.dto.items.track.SpotifyTrackItem;
import lombok.Value;

import java.net.URL;
import java.util.List;

@Value
public class UserTopTracksResponse implements SpotifyItemsResponse {
  URL href;
  List<SpotifyTrackItem> items;
  Integer limit;
  Integer offset;
  Integer total;
  String next;
  String previous;

  @JsonCreator
  public UserTopTracksResponse(
      final URL href,
      final List<SpotifyTrackItem> items,
      final Integer limit,
      final Integer offset,
      final Integer total,
      final String next,
      final String previous) {
    this.href = href;
    this.items = items;
    this.limit = limit;
    this.offset = offset;
    this.total = total;
    this.next = next;
    this.previous = previous;
  }
}

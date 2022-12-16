package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import lombok.Value;

import java.net.URL;
import java.util.List;

@Value
public class TopUserItemsResponse implements SpotifyItemsResponse {
  URL href;
  List<SpotifyItem> items;
  Integer limit;
  Integer offset;
  Integer total;
  String next;
  String previous;

  @JsonCreator
  public TopUserItemsResponse(
      final URL href,
      final List<SpotifyItem> items,
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

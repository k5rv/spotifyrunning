package com.ksaraev.spotifyrunning.client.dto.items.playlist;

import lombok.Value;

import java.net.URL;
import java.util.List;

@Value
public class PlaylistContent<T> {
  URL href;
  List<T> items;
  Integer limit;
  Integer offset;
  Integer total;
  String next;
  String previous;
}

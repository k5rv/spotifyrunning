package com.ksaraev.spotifyrunning.client.dto.items.playlist.content;

import lombok.Value;

import java.net.URL;
import java.util.List;

@Value
public class SpotifyPlaylistContentDTO implements SpotifyPlaylistItemContent {
  URL href;
  List<SpotifyAddableTrackItem> items;
  Integer limit;
  Integer offset;
  Integer total;
  String next;
  String previous;
}

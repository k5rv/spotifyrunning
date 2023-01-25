package com.ksaraev.spotifyrunning.client.dto.items.playlist.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.net.URL;
import java.util.List;

@JsonDeserialize(as = SpotifyPlaylistContentDTO.class)
public interface SpotifyPlaylistItemContent {

  URL getHref();

  List<SpotifyAddableTrackItem> getItems();

  Integer getLimit();

  Integer getOffset();

  Integer getTotal();

  String getNext();

  String getPrevious();
}

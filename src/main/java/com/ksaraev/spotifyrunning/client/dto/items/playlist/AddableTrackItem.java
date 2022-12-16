package com.ksaraev.spotifyrunning.client.dto.items.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyAddableContent;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPreview;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import lombok.Value;

import java.util.Map;

@Value
public class AddableTrackItem implements SpotifyAddableContent, SpotifyPreview {

  @JsonProperty("added_at")
  String addedAt;

  @JsonProperty("added_by")
  SpotifyItem addedBy;

  @JsonProperty("is_local")
  Boolean isLocal;

  @JsonProperty("primary_color")
  String primaryColor;

  @JsonProperty("video_thumbnail")
  Map<String, Object> videoThumbnail;

  @JsonProperty("track")
  TrackItem trackItem;

  @Override
  public SpotifyItem getSpotifyItem() {
    return this.trackItem;
  }
}

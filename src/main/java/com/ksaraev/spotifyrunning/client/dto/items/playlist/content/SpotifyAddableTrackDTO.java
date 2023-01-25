package com.ksaraev.spotifyrunning.client.dto.items.playlist.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.track.SpotifyTrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.SpotifyUserProfileItem;
import lombok.Value;

import java.util.Map;

@Value
public class SpotifyAddableTrackDTO implements SpotifyAddableTrackItem {

  @JsonProperty("added_at")
  String addedAt;

  @JsonProperty("added_by")
  SpotifyUserProfileItem addedBy;

  @JsonProperty("is_local")
  Boolean isLocal;

  @JsonProperty("primary_color")
  String primaryColor;

  @JsonProperty("video_thumbnail")
  Map<String, Object> videoThumbnail;

  @JsonProperty("track")
  SpotifyTrackItem spotifyTrackItem;
}

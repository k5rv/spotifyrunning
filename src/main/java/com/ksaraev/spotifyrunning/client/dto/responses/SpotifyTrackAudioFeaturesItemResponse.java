package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.dto.items.audiofeatures.SpotifyTrackAudioFeaturesItem;
import lombok.Value;

import java.util.List;

@Value
public class SpotifyTrackAudioFeaturesItemResponse implements SpotifyItemsResponse {
  @JsonProperty("audio_features")
  List<SpotifyTrackAudioFeaturesItem> items;

  @JsonCreator
  public SpotifyTrackAudioFeaturesItemResponse(final List<SpotifyTrackAudioFeaturesItem> items) {
    this.items = items;
  }
}

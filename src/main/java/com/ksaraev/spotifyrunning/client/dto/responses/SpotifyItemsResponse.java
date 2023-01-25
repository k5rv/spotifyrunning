package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
  @JsonSubTypes.Type(value = UserTopTracksResponse.class),
  @JsonSubTypes.Type(value = UserRecommendedTracksResponse.class),
  @JsonSubTypes.Type(value = ArtistsResponse.class),
  @JsonSubTypes.Type(value = SpotifyTrackAudioFeaturesItemResponse.class)
})
public interface SpotifyItemsResponse {
  @NotNull
  <T extends SpotifyItem> List<T> getItems();
}

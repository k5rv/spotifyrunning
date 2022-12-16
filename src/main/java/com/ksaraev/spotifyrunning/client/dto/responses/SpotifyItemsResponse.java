package com.ksaraev.spotifyrunning.client.dto.responses;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
  @JsonSubTypes.Type(value = ArtistItemsResponse.class),
  @JsonSubTypes.Type(value = UserRecommendedItemsResponse.class),
  @JsonSubTypes.Type(value = TopUserItemsResponse.class)
})
public interface SpotifyItemsResponse {
  @NotNull
  <T extends SpotifyItem> List<T> getItems();
}

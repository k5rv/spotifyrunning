package com.ksaraev.spotifyrunning.client.dto.items;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItem;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItemDetails;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
  @JsonSubTypes.Type(value = PlaylistItem.class),
  @JsonSubTypes.Type(value = PlaylistItemDetails.class)
})
public interface SpotifyItemDetails {

  @NotNull
  String getName();

  String getDescription();

  Boolean getIsPublic();

  Boolean getIsCollaborative();
}

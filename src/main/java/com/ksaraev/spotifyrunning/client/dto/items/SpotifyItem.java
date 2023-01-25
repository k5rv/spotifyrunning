package com.ksaraev.spotifyrunning.client.dto.items;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.net.URL;

// @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
// @JsonSubTypes({
//  @JsonSubTypes.Type(value = SpotifyTrackDTO.class, name = "track"),
//  @JsonSubTypes.Type(value = SpotifyArtistDTO.class, name = "artist"),
//  @JsonSubTypes.Type(value = AlbumItem.class, name = "album"),
//  @JsonSubTypes.Type(value = SpotifyUserProfileDTO.class, name = "user"),
//  @JsonSubTypes.Type(value = PlaylistItem.class, name = "playlist"),
//  @JsonSubTypes.Type(value = AudioFeaturesItem.class, name = "audio_features")
// })
public interface SpotifyItem {

  @NotEmpty
  String getId();

  @NotEmpty
  String getType();

  @NotNull
  URI getUri();

  URL getHref();
}

package com.ksaraev.spotifyrunning.client.dto.items;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ksaraev.spotifyrunning.client.dto.items.album.AlbumItem;
import com.ksaraev.spotifyrunning.client.dto.items.artist.ArtistItem;
import com.ksaraev.spotifyrunning.client.dto.items.audiofeatures.AudioFeaturesItem;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItem;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.UserProfileItem;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = TrackItem.class, name = "track"),
  @JsonSubTypes.Type(value = ArtistItem.class, name = "artist"),
  @JsonSubTypes.Type(value = AlbumItem.class, name = "album"),
  @JsonSubTypes.Type(value = UserProfileItem.class, name = "user"),
  @JsonSubTypes.Type(value = PlaylistItem.class, name = "playlist"),
  @JsonSubTypes.Type(value = AudioFeaturesItem.class, name = "audio_features")
})
public interface SpotifyItem {

  @NotNull
  String getId();

  @NotNull
  String getType();

  @NotNull
  URI getUri();

  URL getHref();
}

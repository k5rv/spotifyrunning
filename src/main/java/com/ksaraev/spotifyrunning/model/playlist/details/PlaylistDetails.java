package com.ksaraev.spotifyrunning.model.playlist.details;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistDetails implements SpotifyPlaylistDetails {
  private String name;
  private Boolean isPublic;
  private String description;
  private Boolean isCollaborative;
}

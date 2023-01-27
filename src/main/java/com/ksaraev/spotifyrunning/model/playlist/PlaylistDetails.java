package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistDetails implements SpotifyPlaylistDetails {
  @NotEmpty private String name;
  private Boolean isPublic;
  private String description;
  private Boolean isCollaborative;
}

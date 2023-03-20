package com.ksaraev.spotifyrun.model.playlistdetails;

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

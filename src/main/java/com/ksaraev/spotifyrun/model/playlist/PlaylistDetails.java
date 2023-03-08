package com.ksaraev.spotifyrun.model.playlist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class PlaylistDetails implements SpotifyPlaylistDetails {
  @NotEmpty private String name;
  private Boolean isPublic;
  private String description;
  private Boolean isCollaborative;
}

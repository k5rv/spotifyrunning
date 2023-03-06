package com.ksaraev.spotifyrun.model.playlist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Playlist implements SpotifyPlaylist {
  @NotNull private String id;
  @NotEmpty private String name;
  @NotNull private URI uri;
  private String description;
  private String snapshotId;
  private Boolean isPublic;
  private Boolean isCollaborative;
  @NotNull private SpotifyUser owner;
  private List<SpotifyTrack> tracks;
}

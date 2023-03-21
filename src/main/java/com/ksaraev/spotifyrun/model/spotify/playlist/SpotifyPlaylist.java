package com.ksaraev.spotifyrun.model.spotify.playlist;

import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotifyPlaylist implements SpotifyPlaylistItem {
  @NotNull private String id;
  @NotEmpty private String name;
  @NotNull private URI uri;
  private String description;
  @NotNull private String snapshotId;
  private Boolean isPublic;
  private Boolean isCollaborative;
  @Valid @NotNull private SpotifyUserProfileItem owner;
  @Valid private List<SpotifyTrackItem> tracks;
}

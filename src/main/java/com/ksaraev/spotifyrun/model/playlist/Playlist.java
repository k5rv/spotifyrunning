package com.ksaraev.spotifyrun.model.playlist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class Playlist implements SpotifyPlaylist {
  @NotNull private String id;
  private String name;
  private URI uri;
  private String description;
  private String snapshotId;
  private Boolean isPublic;
  private Boolean isCollaborative;
  private SpotifyUser owner;
  private List<SpotifyTrack> tracks;
}

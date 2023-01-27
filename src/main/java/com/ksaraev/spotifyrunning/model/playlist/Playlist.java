package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
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

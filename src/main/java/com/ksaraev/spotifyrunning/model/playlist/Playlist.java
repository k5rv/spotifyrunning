package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playlist implements SpotifyPlaylist {
  private String id;
  private String name;
  private URI uri;
  private String description;
  private String snapshotId;
  private Boolean isPublic;
  private Boolean isCollaborative;
  private SpotifyUser owner;
  private List<SpotifyTrack> tracks;

  @Override
  public String toString() {
    return "Playlist(id:%s, name:%s, snapshotId:%s)".formatted(this.id, this.name, this.snapshotId);
  }
}

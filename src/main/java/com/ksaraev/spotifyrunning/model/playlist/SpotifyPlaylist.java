package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyPlaylist extends SpotifyEntity, SpotifyPlaylistDetails {
  @NotNull
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUser getOwner();

  void setOwner(SpotifyUser spotifyUser);

  List<SpotifyTrack> getTracks();

  void setTracks(List<SpotifyTrack> spotifyTracks);
}

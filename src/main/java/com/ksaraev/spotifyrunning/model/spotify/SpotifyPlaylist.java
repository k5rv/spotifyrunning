package com.ksaraev.spotifyrunning.model.spotify;

import java.util.List;

public interface SpotifyPlaylist extends SpotifyEntity, SpotifyPlaylistDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUser getOwner();

  void setOwner(SpotifyUser spotifyUser);

  List<SpotifyTrack> getTracks();

  void setTracks(List<SpotifyTrack> spotifyTracks);
}

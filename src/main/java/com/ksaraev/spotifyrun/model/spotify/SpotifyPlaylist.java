package com.ksaraev.spotifyrun.model.spotify;

import java.util.List;

public interface SpotifyPlaylist extends SpotifyItem, SpotifyPlaylistDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUser getOwner();

  void setOwner(SpotifyUser user);

  List<SpotifyTrack> getTracks();

  void setTracks(List<SpotifyTrack> tracks);
}

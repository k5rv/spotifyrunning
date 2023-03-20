package com.ksaraev.spotifyrun.model.playlist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyItem;
import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import java.util.List;

public interface SpotifyPlaylist extends SpotifyItem, SpotifyPlaylistDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUser getOwner();

  void setOwner(SpotifyUser user);

  List<SpotifyTrack> getTracks();

  void setTracks(List<SpotifyTrack> tracks);
}

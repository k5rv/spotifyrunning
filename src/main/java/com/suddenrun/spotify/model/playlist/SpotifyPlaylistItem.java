package com.suddenrun.spotify.model.playlist;

import com.suddenrun.spotify.model.SpotifyItem;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.util.List;

public interface SpotifyPlaylistItem extends SpotifyItem, SpotifyPlaylistItemDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUserProfileItem getOwner();

  void setOwner(SpotifyUserProfileItem user);

  List<SpotifyTrackItem> getTracks();

  void setTracks(List<SpotifyTrackItem> tracks);
}

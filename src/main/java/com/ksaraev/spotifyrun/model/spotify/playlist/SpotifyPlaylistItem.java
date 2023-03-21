package com.ksaraev.spotifyrun.model.spotify.playlist;

import com.ksaraev.spotifyrun.model.SpotifyItem;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import java.util.List;

public interface SpotifyPlaylistItem extends SpotifyItem, SpotifyPlaylistItemDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUserProfileItem getOwner();

  void setOwner(SpotifyUserProfileItem user);

  List<SpotifyTrackItem> getTracks();

  void setTracks(List<SpotifyTrackItem> tracks);
}

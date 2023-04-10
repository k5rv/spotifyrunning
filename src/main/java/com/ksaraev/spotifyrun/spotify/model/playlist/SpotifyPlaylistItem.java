package com.ksaraev.spotifyrun.spotify.model.playlist;

import com.ksaraev.spotifyrun.spotify.model.SpotifyItem;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.util.List;

public interface SpotifyPlaylistItem extends SpotifyItem, SpotifyPlaylistItemDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUserProfileItem getOwner();

  void setOwner(SpotifyUserProfileItem user);

  List<SpotifyTrackItem> getTracks();

  void setTracks(List<SpotifyTrackItem> tracks);
}

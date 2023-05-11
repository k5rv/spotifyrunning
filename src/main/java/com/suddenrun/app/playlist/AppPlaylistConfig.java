package com.suddenrun.app.playlist;

import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;

public interface AppPlaylistConfig {

  SpotifyPlaylistItemDetails getDetails();

  SpotifyTrackItemFeatures getMusicFeatures();

  Integer getSize();
}

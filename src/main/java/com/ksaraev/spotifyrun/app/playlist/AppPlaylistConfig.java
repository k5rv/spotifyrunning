package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;

public interface AppPlaylistConfig {

  SpotifyPlaylistItemDetails getDetails();

  SpotifyTrackItemFeatures getMusicFeatures();

  Integer getSize();
}

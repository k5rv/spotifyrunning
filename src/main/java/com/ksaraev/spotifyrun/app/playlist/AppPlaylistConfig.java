package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackItemFeatures;

public interface AppPlaylistConfig {

  SpotifyPlaylistItemDetails getDetails();

  SpotifyTrackItemFeatures getMusicFeatures();

  Integer getSize();
}

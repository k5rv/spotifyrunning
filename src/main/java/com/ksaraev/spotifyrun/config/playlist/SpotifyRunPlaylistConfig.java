package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackItemFeatures;

public interface SpotifyRunPlaylistConfig {

  SpotifyPlaylistItemDetails getDetails();

  SpotifyTrackItemFeatures getMusicFeatures();

  Integer getSize();
}

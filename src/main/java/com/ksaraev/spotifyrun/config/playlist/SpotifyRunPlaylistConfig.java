package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.trackfeatures.SpotifyTrackFeatures;

public interface SpotifyRunPlaylistConfig {

  SpotifyPlaylistDetails getDetails();

  SpotifyTrackFeatures getMusicFeatures();

  Integer getSize();
}

package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;

public interface SpotifyRunPlaylistConfig {

  SpotifyPlaylistDetails getDetails();

  SpotifyTrackFeatures getMusicFeatures();

  Integer getSizeLimit();
}

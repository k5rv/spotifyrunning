package com.ksaraev.spotifyrunning.config.playlist;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;

public interface SpotifyRunningWorkoutPlaylistConfig {

  SpotifyPlaylistDetails getDetails();

  SpotifyTrackFeatures getMusicFeatures();

  Integer getSizeLimit();
}

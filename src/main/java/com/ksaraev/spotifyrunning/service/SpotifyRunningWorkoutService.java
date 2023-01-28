package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SpotifyRunningWorkoutService {

  @Valid
  SpotifyPlaylist createPlaylist(
      @Valid SpotifyPlaylistDetails playlistDetails, @NotNull SpotifyTrackFeatures trackFeatures);
}

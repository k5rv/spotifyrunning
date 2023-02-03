package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SpotifyRunService {

  @Valid
  SpotifyPlaylist createPlaylist(
      @Valid SpotifyPlaylistDetails playlistDetails, @NotNull SpotifyTrackFeatures trackFeatures);
}

package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyPlaylistService {

  SpotifyPlaylist createPlaylist(
      @Valid @NotNull SpotifyUser user, @Valid @NotNull SpotifyPlaylistDetails playlistDetails);

  SpotifyPlaylist getPlaylist(@NotNull String playlistId);

  void addTracks(
      @Valid @NotNull SpotifyPlaylist playlist, @NotEmpty List<@Valid SpotifyTrack> tracks);
}

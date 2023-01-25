package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyPlaylistService {

  SpotifyPlaylist getPlaylist(@NotNull String playlistId);

  SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails);

  void addTracks(@NotNull SpotifyPlaylist playlist, @NotNull List<SpotifyTrack> tracks);
}

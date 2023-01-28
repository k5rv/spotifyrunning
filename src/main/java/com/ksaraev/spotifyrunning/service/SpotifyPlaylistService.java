package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyPlaylistService {

  @Valid
  SpotifyPlaylist createPlaylist(
      @Valid SpotifyUser user, @Valid SpotifyPlaylistDetails playlistDetails);

  @Valid
  SpotifyPlaylist getPlaylist(@NotNull String playlistId);

  void addTracks(@Valid SpotifyPlaylist playlist, List<@Valid SpotifyTrack> tracks);
}

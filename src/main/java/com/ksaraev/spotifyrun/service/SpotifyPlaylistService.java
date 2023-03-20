package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface SpotifyPlaylistService {

  SpotifyPlaylist getPlaylist(@NotNull String playlistId);

  SpotifyPlaylist createPlaylist(
      @Valid @NotNull SpotifyUser user, @Valid @NotNull SpotifyPlaylistDetails playlistDetails);

  void addTracks(
      @Valid @NotNull SpotifyPlaylist playlist,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrack> tracks);
}

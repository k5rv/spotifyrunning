package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface SpotifyPlaylistService {

  SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails);

  void addTracks(@NotNull SpotifyPlaylist playlist, @NotNull List<SpotifyTrack> tracks);

  SpotifyPlaylist getPlaylist(@NotNull String playlistId);
}

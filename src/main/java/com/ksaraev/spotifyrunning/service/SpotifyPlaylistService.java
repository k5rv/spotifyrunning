package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface SpotifyPlaylistService {

  SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails);

  SpotifyPlaylist addTracks(@NotNull SpotifyPlaylist playlist, @NotEmpty List<SpotifyTrack> tracks);

  SpotifyPlaylist getPlaylist(@Valid @NotNull SpotifyPlaylist playlist);
}

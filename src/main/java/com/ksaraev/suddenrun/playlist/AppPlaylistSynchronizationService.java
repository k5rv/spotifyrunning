package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.suddenrun.track.AppTrack;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface AppPlaylistSynchronizationService {

  AppPlaylist updateFromSource(
      @NotNull AppPlaylist targetPlaylist, @NotNull AppPlaylist sourcePlaylist);

  List<SpotifyTrackItem> getTracksToAdd(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> addedTracks);

  List<SpotifyTrackItem> getTracksToRemove(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> removedTracks);
}

package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.suddenrun.track.AppTrack;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface AppPlaylistRevisionService {

  AppPlaylist updatePlaylist(
      @NotNull AppPlaylist sourcePlaylist, @NotNull AppPlaylist actualPlaylist);

  List<AppTrack> getAddedTracks(
      @NotNull AppPlaylist targetPlaylist, @NotNull SpotifyPlaylistItem sourcePlaylist);

  List<AppTrack> getRemovedTracks(
      @NotNull AppPlaylist targetPlaylist, @NotNull SpotifyPlaylistItem sourcePlaylist);

  List<SpotifyTrackItem> getTracksToAdd(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> addedTracks);

  List<SpotifyTrackItem> getTracksToRemove(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> removedTracks);
}

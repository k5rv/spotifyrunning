package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.suddenrun.track.AppTrack;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface AppPlaylistRevisionService {

  List<AppTrack> getAddedSourceTracks(
      @NotNull AppPlaylist appPlaylist, @NotNull SpotifyPlaylistItem spotifyPlaylist);

  List<AppTrack> getRemovedSourceTracks(
      @NotNull AppPlaylist appPlaylist, @NotNull SpotifyPlaylistItem spotifyPlaylist);

  List<SpotifyTrackItem> getSourceTracksToAdd(
          @NotNull List<AppTrack> appTracks,
          @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
          @NotNull List<AppTrack> rejectedTracks);

  List<SpotifyTrackItem> getSourceTracksToRemove(
          @org.jetbrains.annotations.NotNull List<AppTrack> appTracks,
          @org.jetbrains.annotations.NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
          @org.jetbrains.annotations.NotNull List<AppTrack> customTracks);
}

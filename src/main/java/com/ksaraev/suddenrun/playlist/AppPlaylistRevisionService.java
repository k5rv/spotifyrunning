package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.suddenrun.track.AppTrack;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface AppPlaylistRevisionService {

    List<AppTrack> reviseCustomTracks(
            @NotNull AppPlaylist appPlaylist, @NotNull SpotifyPlaylistItem spotifyPlaylist);

    List<AppTrack> reviseRejectedTracks(
            @NotNull AppPlaylist appPlaylist, @NotNull SpotifyPlaylistItem spotifyPlaylist);

}

package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface SpotifyPlaylistItemService {

  SpotifyPlaylistItem getPlaylist(@NotNull String playlistId);

  SpotifyPlaylistItem createPlaylist(
      @Valid @NotNull SpotifyUserProfileItem user, @Valid @NotNull SpotifyPlaylistItemDetails playlistDetails);

  String addTracks(
      @Valid @NotNull String playlistId,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> tracks);

  String removeTracks(
          @Valid @NotNull String playlistId,
          @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> tracks);


}

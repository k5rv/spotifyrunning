package com.suddenrun.spotify.service;

import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface SpotifyPlaylistItemService {

  List<SpotifyPlaylistItem> getUserPlaylists(@Valid @NotNull SpotifyUserProfileItem userProfileItem);

  SpotifyPlaylistItem getPlaylist(@NotNull String playlistId);

  SpotifyPlaylistItem createPlaylist(
      @Valid @NotNull SpotifyUserProfileItem user, @Valid @NotNull SpotifyPlaylistItemDetails playlistItemDetails);

  String addTracks(
      @Valid @NotNull String playlistId,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> trackItems);

  String removeTracks(
          @Valid @NotNull String playlistId,
          @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> trackItems);


}

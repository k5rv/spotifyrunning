package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.AddItemsRequest;
import com.ksaraev.spotifyrun.client.api.AddItemsResponse;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.exception.business.AddTracksException;
import com.ksaraev.spotifyrun.exception.business.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.business.GetPlaylistException;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class SpotifyPlaylistService implements SpotifyPlaylistItemService {

  private final SpotifyClient spotifyClient;
  private final SpotifyPlaylistMapper playlistMapper;

  @Override
  public SpotifyPlaylistItem getPlaylist(String playlistId) throws GetPlaylistException {
    try {
      SpotifyPlaylistDto playlistItem = spotifyClient.getPlaylist(playlistId);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (RuntimeException e) {
      throw new GetPlaylistException(UNABLE_TO_GET_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public SpotifyPlaylistItem createPlaylist(
      SpotifyUserProfileItem user, SpotifyPlaylistItemDetails playlistDetails) {
    try {
      SpotifyPlaylistDetailsDto playlistItemDetails =
          playlistMapper.mapToPlaylistItemDetails(playlistDetails);
      SpotifyPlaylistDto playlistItem =
          spotifyClient.createPlaylist(user.getId(), playlistItemDetails);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (RuntimeException e) {
      throw new CreatePlaylistException(UNABLE_TO_CREATE_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public String addTracks(String playlistId, List<SpotifyTrackItem> tracks) {
    try {
      List<URI> trackUris = tracks.stream().map(SpotifyTrackItem::getUri).toList();
      AddItemsRequest request = new AddItemsRequest(trackUris);
      AddItemsResponse addItemsResponse =
          spotifyClient.addItemsToPlaylist(playlistId, request);
      return addItemsResponse.snapshotId();
    } catch (RuntimeException e) {
      throw new AddTracksException(UNABLE_TO_ADD_TRACKS + e.getMessage(), e);
    }
  }
}

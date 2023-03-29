package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.DeleteTracksException.*;
import static com.ksaraev.spotifyrun.exception.business.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.GetPlaylistException.UNABLE_TO_GET_USER_PLAYLISTS;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.*;
import com.ksaraev.spotifyrun.client.feign.exception.http.SpotifyNotFoundException;
import com.ksaraev.spotifyrun.exception.business.*;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
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
  public List<SpotifyPlaylistItem> getUserPlaylists(SpotifyUserProfileItem userProfileItem) {
    try {
      String userId = userProfileItem.getId();
      GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
      GetUserPlaylistsResponse response = spotifyClient.getPlaylists(userId, request);

      List<SpotifyPlaylistDto> playlistDtos =
          response.playlistItems().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return playlistDtos.isEmpty()
          ? List.of()
          : playlistDtos.stream()
              .map(playlistMapper::mapToPlaylist)
              .map(SpotifyPlaylistItem.class::cast)
              .toList();

    } catch (RuntimeException e) {
      throw new GetPlaylistException(UNABLE_TO_GET_USER_PLAYLISTS + e.getMessage(), e);
    }
  }

  @Override
  public SpotifyPlaylistItem getPlaylist(String playlistId) throws GetPlaylistException {
    try {
      SpotifyPlaylistDto playlistItem = spotifyClient.getPlaylist(playlistId);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (SpotifyNotFoundException e) {
      return null;
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
      UpdateItemsRequest request = new UpdateItemsRequest(trackUris);
      UpdateItemsResponse updateItemsResponse = spotifyClient.addPlaylistItems(playlistId, request);
      return updateItemsResponse.snapshotId();
    } catch (RuntimeException e) {
      throw new AddTracksException(UNABLE_TO_ADD_TRACKS + e.getMessage(), e);
    }
  }

  @Override
  public String removeTracks(String playlistId, List<SpotifyTrackItem> tracks) {
    try {
      List<URI> trackUris = tracks.stream().map(SpotifyTrackItem::getUri).toList();
      UpdateItemsRequest request = new UpdateItemsRequest(trackUris);
      UpdateItemsResponse updateItemsResponse =
          spotifyClient.deletePlaylistItems(playlistId, request);
      return updateItemsResponse.snapshotId();
    } catch (RuntimeException e) {
      throw new DeleteTracksException(UNABLE_TO_DELETE_TRACKS + e.getMessage(), e);
    }
  }
}

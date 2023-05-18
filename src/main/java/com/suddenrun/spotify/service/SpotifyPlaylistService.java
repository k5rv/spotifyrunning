package com.suddenrun.spotify.service;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.*;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.suddenrun.spotify.exception.*;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.net.URI;
import java.util.List;
import java.util.Objects;
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
  private final AddSpotifyPlaylistItemsRequestConfig updatePlaylistItemsRequestConfig;

  @Override
  public List<SpotifyPlaylistItem> getUserPlaylists(SpotifyUserProfileItem userProfileItem) {
    String userId = userProfileItem.getId();
    try {
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

    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyPlaylistServiceGetUserPlaylistsException(userId, e);
    }
  }

  @Override
  public SpotifyPlaylistItem getPlaylist(String playlistId) {
    try {
      SpotifyPlaylistDto playlistItem = spotifyClient.getPlaylist(playlistId);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyPlaylistServiceGetPlaylistException(playlistId, e);
    }
  }

  @Override
  public SpotifyPlaylistItem createPlaylist(
      SpotifyUserProfileItem spotifyUserProfile,
      SpotifyPlaylistItemDetails spotifyPlaylistDetails) {
    String userId = spotifyUserProfile.getId();
    try {
      SpotifyPlaylistDetailsDto playlistItemDetails =
          playlistMapper.mapToPlaylistItemDetails(spotifyPlaylistDetails);
      SpotifyPlaylistDto playlistItem = spotifyClient.createPlaylist(userId, playlistItemDetails);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyPlaylistServiceCreatePlaylistException(userId, e);
    }
  }

  @Override
  public String addTracks(String playlistId, List<SpotifyTrackItem> tracks) {
    try {
      List<URI> trackUris = tracks.stream().map(SpotifyTrackItem::getUri).toList();
      UpdatePlaylistItemsRequest request =
          UpdatePlaylistItemsRequest.builder()
              .itemUris(trackUris)
              .position(updatePlaylistItemsRequestConfig.getPosition())
              .build();
      UpdateUpdateItemsResponse response = spotifyClient.addPlaylistItems(playlistId, request);
      return response.snapshotId();
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyPlaylistServiceAddTracksException(playlistId, e);
    }
  }

  @Override
  public String removeTracks(String playlistId, List<SpotifyTrackItem> tracks) {
    try {
      List<URI> trackUris = tracks.stream().map(SpotifyTrackItem::getUri).toList();
      RemovePlaylistItemsRequest request =
          RemovePlaylistItemsRequest.builder().itemUris(trackUris).build();
      RemovePlaylistItemsResponse response = spotifyClient.removePlaylistItems(playlistId, request);
      return response.snapshotId();
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new SpotifyPlaylistServiceRemoveTracksException(playlistId, e);
    }
  }
}

package com.ksaraev.spotifyrun.spotify.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.*;
import com.ksaraev.spotifyrun.client.feign.exception.http.SpotifyNotFoundException;
import com.ksaraev.spotifyrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotifyrun.spotify.exception.refactored.*;
import com.ksaraev.spotifyrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
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

    } catch (RuntimeException e) {
      throw new GetSpotifyUserPlaylistsException(userId, e);
    }
  }

  @Override
  public SpotifyPlaylistItem getPlaylist(String playlistId) {
    try {
      SpotifyPlaylistDto playlistItem = spotifyClient.getPlaylist(playlistId);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (SpotifyNotFoundException e) {
      return null;
    } catch (RuntimeException e) {
      throw new GetSpotifyPlaylistException(playlistId, e);
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
    } catch (RuntimeException e) {
      throw new CreateSpotifyPlaylistException(userId, e);
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
    } catch (RuntimeException e) {
      throw new AddSpotifyPlaylistTracksException(playlistId, e);
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
    } catch (RuntimeException e) {
      throw new RemoveSpotifyPlaylistTracksException(playlistId, e);
    }
  }
}

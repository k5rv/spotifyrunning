package com.ksaraev.spotifyrun.utils;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.*;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;

public class SpotifyClientStub implements SpotifyClient {

  @Override
  public SpotifyUserProfileDto getCurrentUserProfile() {
    return null;
  }

  @Override
  public GetUserTopTracksResponse getUserTopTracks(GetUserTopTracksRequest request) {
    return null;
  }

  @Override
  public GetRecommendationsResponse getRecommendations(GetRecommendationsRequest request) {
    return null;
  }

  @Override
  public GetUserPlaylistsResponse getPlaylists(String userId, GetUserPlaylistsRequest request) {
    return null;
  }

  @Override
  public SpotifyPlaylistDto createPlaylist(
      String userId, SpotifyPlaylistDetailsDto playlistItemDetails) {
    return null;
  }

  @Override
  public SpotifyPlaylistDto getPlaylist(String playlistId) {
    return null;
  }

  @Override
  public UpdateItemsResponse addPlaylistItems(String playlistId, UpdateItemsRequest request) {
    return null;
  }

  @Override
  public UpdateItemsResponse deletePlaylistItems(String playlistId, UpdateItemsRequest request) {
    return null;
  }
}

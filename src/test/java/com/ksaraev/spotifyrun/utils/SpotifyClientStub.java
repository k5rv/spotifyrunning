package com.ksaraev.spotifyrun.utils;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.*;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.client.api.SpotifyUserProfileDto;

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
  public SpotifyPlaylistDto createPlaylist(
      String userId, SpotifyPlaylistDetailsDto playlistItemDetails) {
    return null;
  }

  @Override
  public SpotifyPlaylistDto getPlaylist(String playlistId) {
    return null;
  }

  @Override
  public AddItemsResponse addItemsToPlaylist(String playlistId, AddItemsRequest request) {
    return null;
  }
}

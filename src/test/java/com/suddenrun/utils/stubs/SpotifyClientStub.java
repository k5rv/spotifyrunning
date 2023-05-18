package com.suddenrun.utils.stubs;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.*;
import com.suddenrun.utils.helpers.SpotifyClientHelper;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class SpotifyClientStub implements SpotifyClient {

  @NotNull
  @Override
  public SpotifyUserProfileDto getCurrentUserProfile() {
    return SpotifyClientHelper.getUserProfileDto();
  }

  @NotNull
  @Override
  public GetUserTopTracksResponse getUserTopTracks(@NotNull GetUserTopTracksRequest request) {
    List<SpotifyTrackDto> trackDtos = SpotifyClientHelper.getTrackDtos(1);
    return SpotifyClientHelper.createGetUserTopTracksResponse(trackDtos);
  }

  @NotNull
  @Override
  public GetRecommendationsResponse getRecommendations(@NotNull GetRecommendationsRequest request) {
    List<SpotifyTrackDto> trackDtos = SpotifyClientHelper.getTrackDtos(1);
    return SpotifyClientHelper.createGetRecommendationsResponse(trackDtos);
  }

  @NotNull
  @Override
  public GetUserPlaylistsResponse getPlaylists(
      @NotNull String userId, @NotNull GetUserPlaylistsRequest request) {
    List<SpotifyPlaylistDto> playlistDtos = new ArrayList<>();
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto();
    playlistDtos.add(playlistDto);
    return SpotifyClientHelper.createUserPlaylistResponse(playlistDtos);
  }

  @NotNull
  @Override
  public SpotifyPlaylistDto createPlaylist(
      @NotNull String userId, @NotNull SpotifyPlaylistDetailsDto playlistItemDetails) {
    return SpotifyClientHelper.getPlaylistDto();
  }

  @NotNull
  @Override
  public SpotifyPlaylistDto getPlaylist(@NotNull String playlistId) {
    return SpotifyClientHelper.getPlaylistDto(playlistId);
  }

  @NotNull
  @Override
  public UpdatePlaylistItemsResponse addPlaylistItems(
      @NotNull String playlistId, @NotNull UpdatePlaylistItemsRequest request) {
    return SpotifyClientHelper.createUpdatePlaylistItemsResponse();
  }

  @NotNull
  @Override
  public RemovePlaylistItemsResponse removePlaylistItems(
      @NotNull String playlistId, @NotNull RemovePlaylistItemsRequest request) {
    return SpotifyClientHelper.createRemovePlaylistItemsResponse();
  }
}

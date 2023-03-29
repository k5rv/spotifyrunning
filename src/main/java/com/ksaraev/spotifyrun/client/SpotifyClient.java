package com.ksaraev.spotifyrun.client;

import com.ksaraev.spotifyrun.client.dto.*;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.client.feign.exception.HandleFeignException;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyClientFeignExceptionHandler;
import feign.Headers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@FeignClient(name = "spotify")
public interface SpotifyClient {
  @GetMapping(path = "me")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyUserProfileDto getCurrentUserProfile();

  @GetMapping(path = "me/top/tracks")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  GetUserTopTracksResponse getUserTopTracks(
      @Valid @NotNull @SpringQueryMap GetUserTopTracksRequest request);

  @GetMapping(path = "recommendations")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  GetRecommendationsResponse getRecommendations(
      @NotNull @SpringQueryMap GetRecommendationsRequest request);

  @GetMapping(path = "/users/{user_id}/playlists")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  GetUserPlaylistsResponse getPlaylists(
      @NotNull @PathVariable(value = "user_id") String userId,
      @Valid @NotNull @SpringQueryMap GetUserPlaylistsRequest request);

  @GetMapping(path = "playlists/{playlist_id}")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyPlaylistDto getPlaylist(@NotNull @PathVariable(value = "playlist_id") String playlistId);

  @PostMapping(path = "users/{userId}/playlists")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyPlaylistDto createPlaylist(
      @NotNull @PathVariable(value = "userId") String userId,
      @Valid @NotNull @RequestBody SpotifyPlaylistDetailsDto playlistItemDetails);

  @PostMapping(path = "playlists/{playlist_id}/tracks")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  UpdateItemsResponse addPlaylistItems(
      @NotNull @PathVariable(value = "playlist_id") String playlistId,
      @Valid @NotNull @RequestBody UpdateItemsRequest request);

  @DeleteMapping(path = "playlists/{playlist_id}/tracks")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  UpdateItemsResponse deletePlaylistItems(
      @NotNull @PathVariable(value = "playlist_id") String playlistId,
      @Valid @NotNull @RequestBody UpdateItemsRequest request);
}

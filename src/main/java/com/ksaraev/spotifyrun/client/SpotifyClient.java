package com.ksaraev.spotifyrun.client;

import com.ksaraev.spotifyrun.client.api.*;
import com.ksaraev.spotifyrun.client.feign.exception.HandleFeignException;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyClientFeignExceptionHandler;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@FeignClient(name = "spotify")
public interface SpotifyClient {
  @GetMapping(path = "me")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyUserProfileItem getCurrentUserProfile();

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

  @GetMapping(path = "playlists/{playlist_id}")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyPlaylistItem getPlaylist(@NotNull @PathVariable(value = "playlist_id") String playlistId);

  @PostMapping(path = "users/{userId}/playlists")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyPlaylistItem createPlaylist(
      @NotNull @PathVariable(value = "userId") String userId,
      @Valid @NotNull @RequestBody SpotifyPlaylistItemDetails playlistItemDetails);

  @PostMapping(path = "playlists/{playlist_id}/tracks")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  AddItemsResponse addItemsToPlaylist(
      @NotNull @PathVariable(value = "playlist_id") String playlistId,
      @Valid @NotNull @RequestBody AddItemsRequest request);
}

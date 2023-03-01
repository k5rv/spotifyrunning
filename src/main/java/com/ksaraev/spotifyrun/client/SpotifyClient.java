package com.ksaraev.spotifyrun.client;

import com.ksaraev.spotifyrun.client.exception.HandleFeignException;
import com.ksaraev.spotifyrun.client.exception.SpotifyClientFeignExceptionHandler;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.AddItemsResponse;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
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
  GetRecommendationsResponse getRecommendations(@SpringQueryMap GetRecommendationsRequest request);

  @PostMapping(path = "users/{userId}/playlists")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyPlaylistItem createPlaylist(
      @PathVariable(value = "userId") String userId,
      @RequestBody SpotifyPlaylistItemDetails playlistItemDetails);

  @GetMapping(path = "playlists/{playlist_id}")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  SpotifyPlaylistItem getPlaylist(@PathVariable(value = "playlist_id") String playlistId);

  @PostMapping(path = "playlists/{playlist_id}/tracks")
  @HandleFeignException(SpotifyClientFeignExceptionHandler.class)
  @Valid
  @NotNull
  AddItemsResponse addItemsToPlaylist(
      @PathVariable(value = "playlist_id") String playlistId, @RequestBody AddItemsRequest request);
}

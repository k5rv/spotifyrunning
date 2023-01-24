package com.ksaraev.spotifyrunning.client;

import com.ksaraev.spotifyrunning.client.config.SpotifyClientFeignConfig;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItemDetails;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichSpotifyItemRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.client.exception.HandleFeignException;
import com.ksaraev.spotifyrunning.client.exception.SpotifyExceptionHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@FeignClient(
    name = "spotify",
    url = "https://api.spotify.com/v1",
    configuration = SpotifyClientFeignConfig.class)
public interface SpotifyClient {
  @GetMapping(path = "me")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItem getCurrentUserProfile();

  @GetMapping(path = "me/top/tracks")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItemsResponse getUserTopTracks(
      @Valid @NotNull @SpringQueryMap GetSpotifyUserItemsRequest spotifyUserItemsRequest);

  @GetMapping(path = "me/top/artists")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItemsResponse getUserTopArtists(
      @Valid @NotNull @SpringQueryMap GetSpotifyUserItemsRequest spotifyUserItemsRequest);

  @GetMapping(path = "recommendations")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItemsResponse getRecommendations(
      @Valid @NotNull @SpringQueryMap GetSpotifyUserItemsRequest spotifyUserItemsRequest);

  @GetMapping(path = "artists")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItemsResponse getArtists(
      @Valid @NotNull @RequestParam(value = "ids") GetSpotifyItemsRequest spotifyItemsRequest);

  @GetMapping(path = "/users/{user_id}")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItem getUserProfile(@Valid @NotNull @PathVariable(value = "user_id") String userId);

  @PostMapping(path = "users/{userId}/playlists")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItem createPlaylist(
      @NotNull @PathVariable(value = "userId") String userId,
      @Valid @NotNull @RequestBody SpotifyItemDetails spotifyItemDetails);

  @GetMapping(path = "playlists/{playlist_id}")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @Valid
  SpotifyItem getPlaylist(@NotNull @PathVariable(value = "playlist_id") String playlistId);

  @PostMapping(path = "playlists/{playlist_id}/tracks")
  @HandleFeignException(SpotifyExceptionHandler.class)
  @NotNull
  String addPlaylistItems(
      @NotNull @PathVariable(value = "playlist_id") String playlistId,
      @Valid @NotNull @RequestBody EnrichSpotifyItemRequest spotifyAddItemRequest);

  @GetMapping(path = "audio-features")
  @HandleFeignException(SpotifyExceptionHandler.class)
  SpotifyItemsResponse getAudioFeatures(
      @RequestParam(value = "ids") GetSpotifyItemsRequest spotifyItemsRequest);
}

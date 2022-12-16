package com.ksaraev.spotifyrunning.client;

import com.ksaraev.spotifyrunning.client.config.FeignClientConfig;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItemDetails;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichSpotifyItemRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@FeignClient(
    name = "spotify",
    url = "https://api.spotify.com/v1",
    configuration = FeignClientConfig.class)
public interface SpotifyClient {
  @GetMapping(path = "me")
  @Valid
  SpotifyItem getCurrentUserProfile();

  @GetMapping(path = "me/top/tracks")
  @Valid
  SpotifyItemsResponse getUserTopTracks(
      @Valid @NotNull @SpringQueryMap GetSpotifyUserItemsRequest spotifyUserItemsRequest);

  @GetMapping(path = "me/top/artists")
  @Valid
  SpotifyItemsResponse getUserTopArtists(
      @Valid @NotNull @SpringQueryMap GetSpotifyUserItemsRequest spotifyUserItemsRequest);

  @GetMapping(path = "recommendations")
  @Valid
  SpotifyItemsResponse getRecommendations(
      @Valid @NotNull @SpringQueryMap GetSpotifyUserItemsRequest spotifyUserItemsRequest);

  @GetMapping(path = "artists")
  @Valid
  SpotifyItemsResponse getArtists(
      @Valid @NotNull @RequestParam(value = "ids") GetSpotifyItemsRequest spotifyItemsRequest);

  @PostMapping(path = "users/{userId}/playlists")
  @Valid
  SpotifyItem createPlaylist(
      @NotNull @PathVariable(value = "userId") String userId,
      @Valid @NotNull @RequestBody SpotifyItemDetails spotifyItemDetails);

  @GetMapping(path = "playlists/{playlist_id}")
  @Valid
  SpotifyItem getPlaylist(@NotNull @PathVariable(value = "playlist_id") String playlistId);

  @PostMapping(path = "playlists/{playlist_id}/tracks")
  @NotNull
  String addPlaylistItems(
      @NotNull @PathVariable(value = "playlist_id") String playlistId,
      @Valid @NotNull @RequestBody EnrichSpotifyItemRequest spotifyAddItemRequest);
}

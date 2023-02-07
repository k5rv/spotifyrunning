package com.ksaraev.spotifyrun.client;

import com.ksaraev.spotifyrun.client.exception.HandleFeignException;
import com.ksaraev.spotifyrun.client.exception.SpotifyExceptionHandler;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.AddItemsResponse;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "spotify")
public interface SpotifyClient {
  @GetMapping(path = "me")
  @HandleFeignException(SpotifyExceptionHandler.class)
  SpotifyUserProfileItem getCurrentUserProfile();

  @GetMapping(path = "me/top/tracks")
  @HandleFeignException(SpotifyExceptionHandler.class)
  GetUserTopTracksResponse getUserTopTracks(@SpringQueryMap GetUserTopTracksRequest request);

  @GetMapping(path = "recommendations")
  @HandleFeignException(SpotifyExceptionHandler.class)
  GetRecommendationsResponse getRecommendations(@SpringQueryMap GetRecommendationsRequest request);

  @PostMapping(path = "users/{userId}/playlists")
  @HandleFeignException(SpotifyExceptionHandler.class)
  SpotifyPlaylistItem createPlaylist(
      @PathVariable(value = "userId") String userId,
      @RequestBody SpotifyPlaylistItemDetails playlistItemDetails);

  @GetMapping(path = "playlists/{playlist_id}")
  @HandleFeignException(SpotifyExceptionHandler.class)
  SpotifyPlaylistItem getPlaylist(@PathVariable(value = "playlist_id") String playlistId);

  @PostMapping(path = "playlists/{playlist_id}/tracks")
  @HandleFeignException(SpotifyExceptionHandler.class)
  AddItemsResponse addItemsToPlaylist(
      @PathVariable(value = "playlist_id") String playlistId, @RequestBody AddItemsRequest request);
}

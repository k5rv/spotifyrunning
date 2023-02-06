package com.ksaraev.spotifyrun.client;

import com.ksaraev.spotifyrun.client.exception.HandleFeignException;
import com.ksaraev.spotifyrun.client.exception.SpotifyExceptionHandler;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrun.client.requests.GetItemsRequest;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.responses.GetSeveralArtistsResponse;
import com.ksaraev.spotifyrun.client.responses.GetTrackAudioFeaturesResponse;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "spotify")
public interface SpotifyClient {
  @GetMapping(path = "me")
  @HandleFeignException(SpotifyExceptionHandler.class)
  SpotifyUserProfileItem getCurrentUserProfile();

  @GetMapping(path = "/users/{user_id}")
  @HandleFeignException(SpotifyExceptionHandler.class)
  SpotifyUserProfileItem getUserProfile(@PathVariable(value = "user_id") String userId);

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
  String addItemsToPlaylist(
      @PathVariable(value = "playlist_id") String playlistId, @RequestBody AddItemsRequest request);

  @GetMapping(path = "artists")
  @HandleFeignException(SpotifyExceptionHandler.class)
  GetSeveralArtistsResponse getSeveralArtists(@RequestParam(value = "ids") GetItemsRequest request);

  @GetMapping(path = "audio-features")
  @HandleFeignException(SpotifyExceptionHandler.class)
  GetTrackAudioFeaturesResponse getTracksAudioFeatures(
      @RequestParam(value = "ids") GetItemsRequest request);
}

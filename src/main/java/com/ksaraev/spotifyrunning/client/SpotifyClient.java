package com.ksaraev.spotifyrunning.client;

import com.ksaraev.spotifyrunning.client.config.SpotifyClientFeignConfig;
import com.ksaraev.spotifyrunning.client.exception.HandleFeignException;
import com.ksaraev.spotifyrunning.client.exception.SpotifyExceptionHandler;
import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrunning.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrunning.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrunning.client.requests.GetItemsRequest;
import com.ksaraev.spotifyrunning.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrunning.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrunning.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrunning.client.responses.GetSeveralArtistsResponse;
import com.ksaraev.spotifyrunning.client.responses.GetTrackAudioFeaturesResponse;
import com.ksaraev.spotifyrunning.client.responses.GetUserTopTracksResponse;
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
      @RequestBody SpotifyPlaylistItemDetails spotifyPlaylistItemDetails);

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

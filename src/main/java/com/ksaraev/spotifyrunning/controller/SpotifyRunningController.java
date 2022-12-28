package com.ksaraev.spotifyrunning.controller;

import com.ksaraev.spotifyrunning.config.SpotifyRunningPlaylistConfig;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.service.SpotifyRunningPlaylistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class SpotifyRunningController {

  private final SpotifyRunningPlaylistService runningPlaylistService;

  private final SpotifyRunningPlaylistConfig runningPlaylist;

  @PostMapping(path = "/playlists")
  public SpotifyPlaylist createPlaylist() {
    return runningPlaylistService.createPlaylist(
        runningPlaylist.getSpotifyPlaylistDetails(),
        runningPlaylist.getSpotifyRecommendationFeatures());
  }
}

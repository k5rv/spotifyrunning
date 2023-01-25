package com.ksaraev.spotifyrunning.controller;

import com.ksaraev.spotifyrunning.config.recommendations.SpotifyRecommendationsConfig;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.service.SpotifyRunningService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class SpotifyRunningController {

  private final SpotifyRunningService runningService;

  private final SpotifyRecommendationsConfig recommendationsConfig;

  @PostMapping(path = "api/v1/playlists")
  public SpotifyPlaylist createPlaylist() {
    return runningService.createPlaylist(
        recommendationsConfig.getSpotifyPlaylistDetails(),
        recommendationsConfig.getSpotifyRecommendationFeatures());
  }
}

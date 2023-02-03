package com.ksaraev.spotifyrunning.controller;

import com.ksaraev.spotifyrunning.config.playlist.SpotifyRunningWorkoutPlaylistConfig;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.service.SpotifyRunningWorkoutService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class SpotifyRunningController {

  private final SpotifyRunningWorkoutService runningService;

  private final SpotifyRunningWorkoutPlaylistConfig playlistConfig;

  @PostMapping
  public SpotifyPlaylist createPlaylist() {
    return runningService.createPlaylist(
        playlistConfig.getDetails(), playlistConfig.getMusicFeatures());
  }
}

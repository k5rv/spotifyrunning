package com.ksaraev.spotifyrun.controller;

import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.service.SpotifyRunService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class SpotifyRunController {

  private final SpotifyRunService runService;

  private final SpotifyRunPlaylistConfig playlistConfig;

  @PostMapping
  public SpotifyPlaylist createPlaylist() {
    return runService.createPlaylist(
        playlistConfig.getDetails(), playlistConfig.getMusicFeatures());
  }
}

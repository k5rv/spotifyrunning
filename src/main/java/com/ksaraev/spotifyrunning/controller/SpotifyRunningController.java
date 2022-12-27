package com.ksaraev.spotifyrunning.controller;

import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.service.SpotifyRunningService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class SpotifyRunningController {

  private final SpotifyRunningService spotifyRunningService;

  @PostMapping(path = "/playlists")
  public SpotifyPlaylist createPlaylist() {
    return spotifyRunningService.createPlaylist();
  }
}

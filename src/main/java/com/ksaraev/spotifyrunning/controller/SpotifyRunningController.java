package com.ksaraev.spotifyrunning.controller;

import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.service.SpotifyRunningService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class SpotifyRunningController {

  private final SpotifyRunningService spotifyRunningService;

  @GetMapping(path = "/playlists")
  public SpotifyPlaylist getPlaylist() {

    return spotifyRunningService.getPlaylist();
  }
}

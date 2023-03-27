package com.ksaraev.spotifyrun.app.playlist;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class PlaylistController {

  private final AppPlaylistService playlistService;

  @PostMapping
  public AppPlaylist createPlaylist() {
    return playlistService.createPlaylist();
  }
}

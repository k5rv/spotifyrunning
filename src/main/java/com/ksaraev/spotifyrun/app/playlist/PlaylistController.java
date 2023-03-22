package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.AppUserService;
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

  private final AppUserService userService;
  private final AppPlaylistService playlistService;

  @PostMapping
  public AppPlaylist createPlaylist() {
    boolean isUserExists = userService.isUserExists();
    AppUser user = isUserExists ? userService.getUser() : userService.createUser();

    boolean isPlaylistExists = playlistService.playlistExists(user);

    if (!isPlaylistExists) {
      AppPlaylist playlist = playlistService.createPlaylist(user);
      playlistService.addMusicRecommendations(playlist);
    }

    return playlistService.getPlaylist(user);
  }
}

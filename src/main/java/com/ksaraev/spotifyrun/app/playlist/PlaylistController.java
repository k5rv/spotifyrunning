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
    AppUser user = userService.isUserExists() ? userService.getUser() : userService.createUser();

    AppPlaylist playlist =
        userService.hasPlaylist(user)
            ? playlistService.getPlaylist(user)
            : playlistService.createPlaylist(user);

    boolean hasTracks = !playlist.getTrackIds().isEmpty();

    if (hasTracks) {
      playlistService.updateMusic(playlist);
    } else {
      playlistService.addMusic(playlist);
    }

    return playlistService.getPlaylist(user);
  }
}

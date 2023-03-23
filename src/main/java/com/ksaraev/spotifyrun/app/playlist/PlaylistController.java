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
        playlistService.isRelationExists(user)
            ? playlistService.getPlaylist(user)
            : playlistService.createPlaylist(user);


    playlistService.addMusic(playlist);
    return playlistService.getPlaylist(user);
  }
}
/*
  1. user exist?
  1.1 no -> create user
  1.2 yes -> get user

  2. user has playlist?
  2.1 no -> create playlist
  2.1.2 get tracks
  2.1.3 add tracks to playlist
  2.2 yes -> get tracks
  2.2.1 get playlist
  2.2.2 delete all tracks
  2.2.3 add tracks
*/

package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.AppTrackService;
import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.AppUserGetAuthenticatedException;
import com.ksaraev.spotifyrun.app.user.AppUserService;
import java.util.List;
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

  private final AppTrackService trackService;

  private final AppPlaylistService playlistService;

  @PostMapping
  public AppPlaylist createPlaylist() {
    AppUser appUser =
        userService.getAuthenticatedUser().orElseThrow(AppUserGetAuthenticatedException::new);

    String appUserId = appUser.getId();
    String appUserName = appUser.getName();
    appUser =
        userService
            .getUser(appUserId)
            .orElseGet(() -> userService.registerUser(appUserId, appUserName));

    AppPlaylist appPlaylist = createPlaylistIfNotExists(appUser);
    List<AppTrack> appTracks = trackService.getTracks();
    return playlistService.addTracks(appPlaylist, appTracks);
  }

  private AppPlaylist createPlaylistIfNotExists(AppUser appUser) {
    return playlistService
        .getPlaylist(appUser)
        .orElseGet(() -> playlistService.createPlaylist(appUser));
  }
}

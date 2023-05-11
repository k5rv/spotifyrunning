package com.suddenrun.app.playlist;

import com.suddenrun.app.track.AppTrack;
import com.suddenrun.app.track.AppTrackService;
import com.suddenrun.app.user.AppUser;
import com.suddenrun.app.user.AppUserService;
import com.suddenrun.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.service.SpotifyUserProfileService;
import java.util.List;

import com.suddenrun.app.exception.AppAuthorizationException;
import com.suddenrun.app.exception.AppPlaylistAlreadyExistException;
import com.suddenrun.app.exception.AppPlaylistNotFoundException;
import com.suddenrun.app.exception.AppUserNotRegisteredException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class PlaylistController {

  private final AppUserService userService;

  private final AppTrackService trackService;

  private final AppPlaylistService playlistService;

  private final SpotifyUserProfileService userProfileService;

  @PostMapping
  public AppPlaylist createPlaylist() {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUser();
      String userId = userProfileItem.getId();
      AppUser appUser =
          userService.getUser(userId).orElseThrow(() -> new AppUserNotRegisteredException(userId));
      playlistService
          .getPlaylist(appUser)
          .ifPresent(
              playlist -> {
                throw new AppPlaylistAlreadyExistException(userId, playlist.getId());
              });
      AppPlaylist appPlaylist = createPlaylistIfNotExists(appUser);
      List<AppTrack> appTracks = trackService.getTracks();
      return playlistService.addTracks(appPlaylist, appTracks);
    } catch (SpotifyUnauthorizedException e) {
      throw new AppAuthorizationException(e);
    }
  }

  private AppPlaylist createPlaylistIfNotExists(AppUser appUser) {
    return playlistService
        .getPlaylist(appUser)
        .orElseGet(() -> playlistService.createPlaylist(appUser));
  }

  @PutMapping
  public AppPlaylist updatePlaylist() {
    SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUser();
    String userId = userProfileItem.getId();
    AppUser appUser =
        userService.getUser(userId).orElseThrow(() -> new AppUserNotRegisteredException(userId));
    AppPlaylist appPlaylist =
        playlistService
            .getPlaylist(appUser)
            .orElseThrow(() -> new AppPlaylistNotFoundException(userId));
    List<AppTrack> appTracks = trackService.getTracks();
    return playlistService.addTracks(appPlaylist, appTracks);
  }

  @GetMapping
  public AppPlaylist getPlaylist() {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUser();
      String userId = userProfileItem.getId();
      AppUser appUser =
          userService.getUser(userId).orElseThrow(() -> new AppUserNotRegisteredException(userId));
      return playlistService
          .getPlaylist(appUser)
          .orElseThrow(() -> new AppPlaylistNotFoundException(userId));
    } catch (SpotifyUnauthorizedException e) {
      throw new AppAuthorizationException(e);
    }
  }
}

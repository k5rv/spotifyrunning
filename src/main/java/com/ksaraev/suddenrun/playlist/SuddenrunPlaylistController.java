package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyUserProfileService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackService;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.AppUserService;
import com.ksaraev.suddenrun.user.SuddenrunUserIsNotRegisteredException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class SuddenrunPlaylistController {

  private final AppUserService userService;

  private final AppTrackService trackService;

  private final AppPlaylistService playlistService;

  private final SpotifyUserProfileService userProfileService;

  @PostMapping
  public AppPlaylist createPlaylist() {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      AppUser appUser =
          userService
              .getUser(userId)
              .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
      playlistService
          .getPlaylist(appUser)
          .ifPresent(
              playlist -> {
                throw new SuddenrunPlaylistAlreadyExistsException(playlist.getId());
              });
      AppPlaylist appPlaylist = createPlaylistIfNotExists(appUser);
      List<AppTrack> appTracks = trackService.getTracks();
      return playlistService.addTracks(appPlaylist, appTracks);
    } catch (SpotifyUnauthorizedException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  private AppPlaylist createPlaylistIfNotExists(AppUser appUser) {
    return playlistService
        .getPlaylist(appUser)
        .orElseGet(() -> playlistService.createPlaylist(appUser));
  }

  @PutMapping
  public AppPlaylist updatePlaylist() {
    SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUserProfile();
    String userId = userProfileItem.getId();
    AppUser appUser =
        userService
            .getUser(userId)
            .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
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
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      AppUser appUser =
          userService
              .getUser(userId)
              .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
      return playlistService
          .getPlaylist(appUser)
          .orElseThrow(() -> new AppPlaylistNotFoundException(userId));
    } catch (SpotifyUnauthorizedException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }
}

package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.exception.*;
import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.AppTrackService;
import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.AppUserService;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.spotify.service.SpotifyUserProfileService;
import java.util.List;
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

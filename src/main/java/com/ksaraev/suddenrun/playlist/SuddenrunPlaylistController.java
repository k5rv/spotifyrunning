package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyUserProfileService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
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
      AppPlaylist appPlaylist = createPlaylist(appUser);
      List<AppTrack> appTracks = trackService.getTracks();
      return playlistService.addTracks(appPlaylist, appTracks);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  private AppPlaylist createPlaylist(AppUser appUser) {
    return playlistService.createPlaylist(appUser);
  }

  @PutMapping
  public AppPlaylist updatePlaylist() {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      AppUser appUser =
          userService
              .getUser(userId)
              .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
      AppPlaylist appPlaylist =
          playlistService
              .getPlaylist(appUser)
              .orElseThrow(() -> new SuddenrunUserDoesNotHaveAnyPlaylistsException(userId));
      List<AppTrack> appTracks = trackService.getTracks();
      return playlistService.addTracks(appPlaylist, appTracks);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @PutMapping("/{playlist_id}/tracks")
  public AddPlaylistTracksResponse addTracks(@PathVariable(value = "playlist_id") String playlistId) {
    try {
      AppPlaylist appPlaylist =
          playlistService
              .getPlaylist(playlistId)
              .orElseThrow(() -> new SuddenrunPlaylistDoesNotExistException(playlistId));
      List<AppTrack> appTracks = trackService.getTracks();
      appPlaylist = playlistService.addTracks(appPlaylist, appTracks);
      List<String> trackIds = appPlaylist.getTracks().stream().map(AppTrack::getId).toList();
      return AddPlaylistTracksResponse.builder().id(playlistId).trackIds(trackIds).build();
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
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
          .orElseThrow(() -> new SuddenrunUserDoesNotHaveAnyPlaylistsException(userId));
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }
}

package com.ksaraev.suddenrun.user;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyUserProfileItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.playlist.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class SuddenrunUserController {

  private final AppUserService suddenrunUserService;

  private final AppPlaylistService suddenrunPlaylistService;

  private final SpotifyUserProfileItemService spotifyUserProfileService;

  private final AppUserMapper mapper;

  @GetMapping("/current")
  public GetCurrentUserResponse getCurrentUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUserProfile();
      String id = userProfileItem.getId();
      String name = userProfileItem.getName();
      boolean isRegistered = suddenrunUserService.isUserRegistered(id);
      return GetCurrentUserResponse.builder().id(id).name(name).isRegistered(isRegistered).build();
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @PostMapping(path = "/{user_id}")
  public GetUserResponse registerUser(@NotNull @PathVariable(value = "user_id") String userId) {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUserProfile();
      String spotifyUserId = userProfileItem.getId();
      boolean isMatchAuthenticatedSpotifyUser = spotifyUserId.equals(userId);
      if (!isMatchAuthenticatedSpotifyUser)
        throw new SuddenrunUserDoesNotMatchAuthenticatedSpotifyUserException(userId);
      String spotifyUserName = userProfileItem.getName();
      boolean isUserRegistered = suddenrunUserService.isUserRegistered(userId);
      if (isUserRegistered) throw new SuddenrunUserIsAlreadyRegisteredException(userId);
      AppUser appUser = suddenrunUserService.registerUser(spotifyUserId, spotifyUserName);
      return mapper.mapToDto(appUser);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @PostMapping(path = "/{user_id}/playlists")
  public CreatePlaylistResponse createPlaylist(
      @NotNull @PathVariable(value = "user_id") String userId) {
    try {
      AppUser appUser =
          suddenrunUserService
              .getUser(userId)
              .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
      suddenrunPlaylistService
          .getPlaylist(appUser)
          .ifPresent(
              playlist -> {
                throw new SuddenrunPlaylistAlreadyExistsException(playlist.getId());
              });
      AppPlaylist appPlaylist = suddenrunPlaylistService.createPlaylist(appUser);
      String playlistId = appPlaylist.getId();
      return CreatePlaylistResponse.builder().id(playlistId).build();
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @GetMapping(path = "/{user_id}/playlists")
  public GetUserPlaylistResponse getUserPlaylist(
      @NotNull @PathVariable(value = "user_id") String userId) {
    try {
      AppUser appUser =
          suddenrunUserService
              .getUser(userId)
              .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
      AppPlaylist appPlaylist =
          suddenrunPlaylistService
              .getPlaylist(appUser)
              .orElseThrow(() -> new SuddenrunUserDoesNotHaveAnyPlaylistsException(userId));
      String playlistId = appPlaylist.getId();
      return GetUserPlaylistResponse.builder().id(playlistId).build();
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }
}

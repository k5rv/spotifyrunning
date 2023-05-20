package com.suddenrun.app.user;

import com.suddenrun.app.exception.SuddenrunAuthenticationException;
import com.suddenrun.app.exception.SuddenrunUserIsAlreadyRegisteredException;
import com.suddenrun.app.exception.AppUserNotRegisteredException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.service.SpotifyUserProfileItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class SuddenrunUserController {

  private final AppUserService suddenrunUserService;

  private final SpotifyUserProfileItemService spotifyUserProfileService;

  @GetMapping
  public AppUser getUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      return suddenrunUserService
          .getUser(userId)
          .orElseThrow(() -> new AppUserNotRegisteredException(userId));
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @PostMapping
  public AppUser registerUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      String userName = userProfileItem.getName();
      boolean isUserRegistered = suddenrunUserService.isUserRegistered(userId);
      if (isUserRegistered) throw new SuddenrunUserIsAlreadyRegisteredException(userId);
      return suddenrunUserService.registerUser(userId, userName);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }
}

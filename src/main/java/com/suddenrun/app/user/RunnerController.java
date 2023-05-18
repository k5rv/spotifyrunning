package com.suddenrun.app.user;

import com.suddenrun.app.exception.AppAuthorizationException;
import com.suddenrun.app.exception.AppUserAlreadyRegisteredException;
import com.suddenrun.app.exception.AppUserNotRegisteredException;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.service.SpotifyUserProfileService;
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
public class RunnerController {

  private final AppUserService userService;

  private final SpotifyUserProfileService userProfileService;

  @GetMapping
  public AppUser getUser() {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      return userService
          .getUser(userId)
          .orElseThrow(() -> new AppUserNotRegisteredException(userId));
    } catch (SpotifyUnauthorizedException e) {
      throw new AppAuthorizationException(e);
    }
  }

  @PostMapping
  public AppUser registerUser() {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      String userName = userProfileItem.getName();
      boolean isUserRegistered = userService.isUserRegistered(userId);
      if (isUserRegistered) throw new AppUserAlreadyRegisteredException(userId);
      return userService.registerUser(userId, userName);
    } catch (SpotifyUnauthorizedException e) {
      throw new AppAuthorizationException(e);
    }
  }
}

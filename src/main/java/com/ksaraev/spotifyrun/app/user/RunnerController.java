package com.ksaraev.spotifyrun.app.user;

import com.ksaraev.spotifyrun.app.exception.AppAuthorizationException;
import com.ksaraev.spotifyrun.app.exception.AppUserAlreadyRegisteredException;
import com.ksaraev.spotifyrun.app.exception.AppUserNotRegisteredException;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.spotify.service.SpotifyUserProfileService;
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
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUser();
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
      SpotifyUserProfileItem userProfileItem = userProfileService.getCurrentUser();
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

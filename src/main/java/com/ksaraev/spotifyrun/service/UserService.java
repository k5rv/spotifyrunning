package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.GetUserException.UNABLE_TO_GET_USER;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.business.GetUserException;
import com.ksaraev.spotifyrun.model.user.AppUserMapper;import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements SpotifyUserService {
  private final SpotifyClient spotifyClient;
  private final AppUserMapper appUserMapper;

  @Override
  public SpotifyUser getCurrentUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyClient.getCurrentUserProfile();
      return appUserMapper.mapToUser(userProfileItem);
    } catch (RuntimeException e) {
      throw new GetUserException(UNABLE_TO_GET_USER + e.getMessage(), e);
    }
  }

  @Override
  public SpotifyUser getUser(String userId) {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyClient.getUserProfile(userId);
      return appUserMapper.mapToUser(userProfileItem);
    } catch (RuntimeException e) {
      throw new GetUserException(UNABLE_TO_GET_USER + e.getMessage(), e);
    }
  }
}

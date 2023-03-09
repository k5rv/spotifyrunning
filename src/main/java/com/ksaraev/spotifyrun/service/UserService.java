package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.service.GetUserException.UNABLE_TO_GET_USER;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.service.GetUserException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements SpotifyUserService {
  private final SpotifyClient spotifyClient;
  private final UserMapper userMapper;

  @Override
  public SpotifyUser getCurrentUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyClient.getCurrentUserProfile();
      return userMapper.mapToUser(userProfileItem);
    } catch (RuntimeException e) {
      throw new GetUserException(UNABLE_TO_GET_USER + e.getMessage(), e);
    }
  }
}

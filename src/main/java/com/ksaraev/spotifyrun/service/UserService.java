package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyNotFoundException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.GetUserException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ksaraev.spotifyrun.exception.GetUserException.UNABLE_TO_GET_USER;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED;
import static com.ksaraev.spotifyrun.exception.UserNotFoundException.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements SpotifyUserService {
  private final SpotifyClient spotifyClient;
  private final UserMapper userMapper;

  @Override
  public SpotifyUser getUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyClient.getCurrentUserProfile();
      return userMapper.mapToUser(userProfileItem);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + e.getMessage(), e);
    } catch (SpotifyNotFoundException e) {
      throw new UserNotFoundException(USER_NOT_FOUND + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetUserException(UNABLE_TO_GET_USER + e.getMessage(), e);
    }
  }
}

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.ksaraev.spotifyrun.exception.GetUserException.GET_USER_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.exception.UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MESSAGE;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService implements SpotifyUserService {
  private final SpotifyClient spotifyClient;
  private final UserMapper userMapper;

  @Override
  public @Valid SpotifyUser getUser() {
    SpotifyUserProfileItem userProfileItem;
    try {
      userProfileItem = spotifyClient.getCurrentUserProfile();
      return userMapper.toModel(userProfileItem);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED_EXCEPTION_MESSAGE + ": " + e.getMessage(), e);
    } catch (SpotifyNotFoundException e) {
      throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + ": " + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetUserException(GET_USER_EXCEPTION_MESSAGE + ": " + e.getMessage(), e);
    }
  }
}

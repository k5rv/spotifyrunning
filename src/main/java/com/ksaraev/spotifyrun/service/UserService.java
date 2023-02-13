package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.SpotifyClientException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyNotFoundException;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.exception.UserServiceException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
    } catch (SpotifyNotFoundException e) {
      throw new UserNotFoundException("User not found: " + e.getMessage(), e);
    } catch (SpotifyException e) {
      throw new UserServiceException(
          "Unable to get a user: User service exception occurred: Spotify Web API returned an unsuccessful HTTP status code: "
              + e.getMessage(),
          e);
    } catch (SpotifyClientException e) {
      throw new UserServiceException(
          "Unable to get a user: User service exception occurred: Spotify client returned an error: "
              + e.getMessage(),
          e);
    } catch (RuntimeException e) {
      throw new UserServiceException(
          "Unable to get a user: User service exception occurred: " + e.getMessage(), e);
    }
    if (userProfileItem == null) {
      throw new UserNotFoundException(
          "User not found: Spotify Web API returned null instead of User Profile");
    }
    try {
      return userMapper.toModel(userProfileItem);
    } catch (RuntimeException e) {
      throw new UserServiceException(
          "Unable to get a user: User service exception occurred: Error while mapping from Spotify User Profile to User");
    }
  }
}

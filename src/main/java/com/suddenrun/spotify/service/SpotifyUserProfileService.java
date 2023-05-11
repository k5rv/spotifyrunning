package com.suddenrun.spotify.service;

import com.suddenrun.client.SpotifyClient;
import com.suddenrun.client.dto.SpotifyUserProfileDto;
import com.suddenrun.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.exception.GetSpotifyUserProfileException;
import com.suddenrun.spotify.exception.SpotifyServiceAuthenticationException;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyUserProfileService implements SpotifyUserProfileItemService {
  private final SpotifyClient spotifyClient;
  private final SpotifyUserProfileMapper userMapper;

  @Override
  public SpotifyUserProfileItem getCurrentUser() {
    try {
      SpotifyUserProfileDto userProfileDto = spotifyClient.getCurrentUserProfile();
      return userMapper.mapToModel(userProfileDto);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyServiceAuthenticationException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyUserProfileException(e);
    }
  }
}

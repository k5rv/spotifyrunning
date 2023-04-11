package com.ksaraev.spotifyrun.spotify.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.spotify.exception.GetSpotifyUserProfileException;
import com.ksaraev.spotifyrun.spotify.exception.SpotifyServiceAuthenticationException;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileMapper;
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

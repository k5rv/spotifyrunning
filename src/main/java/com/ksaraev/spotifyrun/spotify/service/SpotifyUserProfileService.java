package com.ksaraev.spotifyrun.spotify.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.client.feign.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.spotify.exception.refactored.GetSpotifyUserProfileException;
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
    } catch (RuntimeException e) {
      throw new GetSpotifyUserProfileException(e);
    }
  }
}

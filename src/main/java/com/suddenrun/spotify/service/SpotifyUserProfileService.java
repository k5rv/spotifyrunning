package com.suddenrun.spotify.service;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.SpotifyUserProfileDto;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.exception.GetSpotifyUserProfileException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyUserProfileService implements SpotifyUserProfileItemService {

  private final SpotifyClient client;

  private final SpotifyUserProfileMapper mapper;

  @Override
  public SpotifyUserProfileItem getCurrentUserProfile() {
    try {
      SpotifyUserProfileDto userProfileDto = client.getCurrentUserProfile();
      return mapper.mapToModel(userProfileDto);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyUserProfileException(e);
    }
  }
}

package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.GetUserException.UNABLE_TO_GET_USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.exception.business.GetUserException;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileMapper;
import com.ksaraev.spotifyrun.security.AuthenticationFacade;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyUserProfileService implements SpotifyUserProfileItemService {
  private final SpotifyClient spotifyClient;
  private final SpotifyUserProfileMapper userMapper;
  private final AuthenticationFacade authenticationFacade;

  @Override
  public SpotifyUserProfileItem getCurrentUser() {
    try {
      SpotifyUserProfileDto userProfileDto = getAuthenticatedUserSpotifyProfile();
      if (userProfileDto == null) userProfileDto = spotifyClient.getCurrentUserProfile();
      return userMapper.mapToUser(userProfileDto);
    } catch (RuntimeException e) {
      throw new GetUserException(UNABLE_TO_GET_USER + e.getMessage(), e);
    }
  }

  private SpotifyUserProfileDto getAuthenticatedUserSpotifyProfile() {
    boolean isAuthenticated = authenticationFacade.getAuthentication().isAuthenticated();
    if (!isAuthenticated) {
      return null;
    }
    OAuth2AuthenticatedPrincipal principal =
        (OAuth2AuthenticatedPrincipal) authenticationFacade.getAuthentication().getPrincipal();
    Map<String, Object> attributes = principal.getAttributes();
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.convertValue(attributes, SpotifyUserProfileDto.class);
  }
}

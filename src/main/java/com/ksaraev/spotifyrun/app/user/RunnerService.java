package com.ksaraev.spotifyrun.app.user;

import static com.ksaraev.spotifyrun.exception.business.GetAppPlaylistException.UNABLE_TO_GET_APP_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.GetAppUserException.*;
import static com.ksaraev.spotifyrun.exception.business.GetRegistrationStatusException.*;
import static com.ksaraev.spotifyrun.exception.business.UserCreationException.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksaraev.spotifyrun.app.playlist.PlaylistRepository;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.exception.business.GetAppPlaylistException;
import com.ksaraev.spotifyrun.exception.business.GetAppUserException;
import com.ksaraev.spotifyrun.exception.business.GetRegistrationStatusException;
import com.ksaraev.spotifyrun.exception.business.UserCreationException;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileMapper;
import com.ksaraev.spotifyrun.security.AuthenticationFacade;
import com.ksaraev.spotifyrun.service.SpotifyUserProfileItemService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RunnerService implements AppUserService {

  private final SpotifyUserProfileItemService spotifyUserProfileService;

  private final AuthenticationFacade authenticationFacade;

  private final RunnerRepository runnerRepository;
  private final PlaylistRepository playlistRepository;

  private final SpotifyUserProfileMapper spotifyUserProfileMapper;
  private final RunnerMapper runnerMapper;

  @Override
  public boolean isUserExists() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
      String id = userProfileItem.getId();
      return runnerRepository.existsById(id);
    } catch (RuntimeException e) {
      throw new GetRegistrationStatusException(
          UNABLE_TO_GET_USER_REGISTRATION_STATUS + e.getMessage(), e);
    }
  }

  @Override
  public boolean isUserExists(String id) {
    try {
      runnerRepository.existsById(id);
      return runnerRepository.existsById(id);
    } catch (RuntimeException e) {
      throw new GetRegistrationStatusException(
          UNABLE_TO_GET_USER_REGISTRATION_STATUS + e.getMessage(), e);
    }
  }

  @Override
  public AppUser createUser() {
    return null;
  }

  @Override
  public AppUser getUser() {
    return null;
  }

  @Override
  public boolean hasPlaylist(AppUser appUser) {
    try {
      String id = appUser.getId();
      return playlistRepository.existsByRunnerId(id);
    } catch (RuntimeException e) {
      throw new GetAppPlaylistException(UNABLE_TO_GET_APP_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public AppUser getUser(String id) {
    Optional<Runner> optionalRunner;
    try {
      //SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
      //String id = userProfileItem.getId();
      optionalRunner = runnerRepository.findById(id);
    } catch (RuntimeException e) {
      throw new GetAppUserException(UNABLE_TO_GET_APP_USER + e.getMessage(), e);
    }
    return optionalRunner.orElseThrow(() -> new GetAppUserException(UNABLE_TO_GET_APP_USER));
  }

  @Override
  public AppUser createUser(String id, String name) {
    try {
//      SpotifyUserProfileItem userProfileItem = getAuthenticatedUserSpotifyProfile();
//      if (userProfileItem == null) {
//        userProfileItem = spotifyUserProfileService.getCurrentUser();
//      }
//      Runner runner = runnerMapper.mapToEntity(userProfileItem);
      Runner runner = Runner.builder().id(id).name(name).build();
      runnerRepository.save(runner);
      return runner;
    } catch (RuntimeException e) {
      throw new UserCreationException(UNABLE_TO_CREATE_USER + e.getMessage(), e);
    }
  }

  private SpotifyUserProfileItem getAuthenticatedUserSpotifyProfile() {
    boolean isAuthenticated = authenticationFacade.getAuthentication().isAuthenticated();
    if (!isAuthenticated) {
      return null;
    }
    OAuth2AuthenticatedPrincipal principal =
        (OAuth2AuthenticatedPrincipal) authenticationFacade.getAuthentication().getPrincipal();
    Map<String, Object> attributes = principal.getAttributes();
    ObjectMapper objectMapper = new ObjectMapper();
    SpotifyUserProfileDto userProfileDto =
        objectMapper.convertValue(attributes, SpotifyUserProfileDto.class);
    return spotifyUserProfileMapper.mapToModel(userProfileDto);
  }
}

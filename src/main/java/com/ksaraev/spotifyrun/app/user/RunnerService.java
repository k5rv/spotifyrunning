package com.ksaraev.spotifyrun.app.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileMapper;
import com.ksaraev.spotifyrun.security.AuthenticationFacade;
import java.util.List;
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
  private final AppUserMapper appUserMapper;
  private final RunnerRepository runnerRepository;
  private final AuthenticationFacade authenticationFacade;
  private final SpotifyUserProfileMapper userProfileMapper;

  @Override
  public boolean isUserRegistered(String userId) {
    try {
      return runnerRepository.existsById(userId);
    } catch (RuntimeException e) {
      throw new AppUserRegistrationStatusSearchingException(userId, e);
    }
  }

  @Override
  public Optional<AppUser> getUser(String userId) {
    try {
      Optional<Runner> optionalAppUser = runnerRepository.findById(userId);
      if (optionalAppUser.isEmpty()) return Optional.empty();
      return optionalAppUser.map(AppUser.class::cast);
    } catch (RuntimeException e) {
      throw new AppUserSearchingException(userId, e);
    }
  }

  @Override
  public AppUser registerUser(String userId, String userName) {
    try {
      Runner runner = Runner.builder().id(userId).name(userName).build();
      runnerRepository.save(runner);
      return runner;
    } catch (RuntimeException e) {
      throw new AppUserRegistrationException(userId, e);
    }
  }

  public Optional<AppUser> getAuthenticatedUser() {
    try {
      boolean isAuthenticated = authenticationFacade.getAuthentication().isAuthenticated();
      if (!isAuthenticated) {
        return Optional.empty();
      }
      OAuth2AuthenticatedPrincipal principal =
          (OAuth2AuthenticatedPrincipal) authenticationFacade.getAuthentication().getPrincipal();
      Map<String, Object> attributes = principal.getAttributes();
      ObjectMapper objectMapper = new ObjectMapper();
      SpotifyUserProfileDto userProfileDto =
          objectMapper.convertValue(attributes, SpotifyUserProfileDto.class);
      SpotifyUserProfileItem userProfileItem = userProfileMapper.mapToModel(userProfileDto);
      AppUser appUser = appUserMapper.mapToEntity(userProfileItem);
      return Optional.ofNullable(appUser);
    } catch (RuntimeException e) {
      throw new AppUserGetAuthenticatedException(e);
    }
  }
}

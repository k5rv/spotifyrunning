package com.ksaraev.spotifyrun.app.user;

import static com.ksaraev.spotifyrun.exception.business.GetAppUserException.*;
import static com.ksaraev.spotifyrun.exception.business.GetRegistrationStatusException.*;
import static com.ksaraev.spotifyrun.exception.business.UserRegistrationException.*;

import com.ksaraev.spotifyrun.exception.business.GetAppUserException;
import com.ksaraev.spotifyrun.exception.business.GetRegistrationStatusException;
import com.ksaraev.spotifyrun.exception.business.UserRegistrationException;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.service.SpotifyUserProfileItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RunnerService implements AppUserService {

  private final SpotifyUserProfileItemService spotifyUserProfileService;

  private final RunnerRepository repository;

  private final RunnerMapper mapper;

  @Override
  public boolean isUserExists() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
      String id = userProfileItem.getId();
      return repository.existsBySpotifyId(id);
    } catch (RuntimeException e) {
      throw new GetRegistrationStatusException(
          UNABLE_TO_GET_USER_REGISTRATION_STATUS + e.getMessage(), e);
    }
  }

  @Override
  public AppUser getUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
      String id = userProfileItem.getId();
      return repository.findBySpotifyId(id);
    } catch (RuntimeException e) {
      throw new GetAppUserException(UNABLE_TO_GET_APP_USER + e.getMessage(), e);
    }
  }

  @Override
  public AppUser createUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
      Runner runner = mapper.mapToEntity(userProfileItem);
      repository.save(runner);
      return runner;
    } catch (RuntimeException e) {
      throw new UserRegistrationException(UNABLE_TO_REGISTER_USER + e.getMessage(), e);
    }
  }
}

package com.ksaraev.spotifyrun.app.runner;

import static com.ksaraev.spotifyrun.exception.business.GetRegistrationStatusException.*;
import static com.ksaraev.spotifyrun.exception.business.UserRegistrationException.*;

import com.ksaraev.spotifyrun.exception.business.GetRegistrationStatusException;
import com.ksaraev.spotifyrun.exception.business.UserRegistrationException;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunnerService implements SpotifyAppUserService {

  private final RunnerRepository repository;

  private final RunnerMapper mapper;

  public boolean isUserRegistered(SpotifyUserProfileItem userProfileItem) {
    try {
      String id = userProfileItem.getId();
      return repository.existsBySpotifyId(id);
    } catch (RuntimeException e) {
      throw new GetRegistrationStatusException(
          UNABLE_TO_GET_USER_REGISTRATION_STATUS + e.getMessage(), e);
    }
  }

  @Override
  public void registerUser(SpotifyUserProfileItem userProfileItem) {
    try {
      Runner runner = mapper.mapToEntity(userProfileItem);
      repository.save(runner);
    } catch (RuntimeException e) {
      throw new UserRegistrationException(
              UNABLE_TO_REGISTER_USER + e.getMessage(), e);
    }
  }
}

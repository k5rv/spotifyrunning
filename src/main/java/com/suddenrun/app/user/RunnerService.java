package com.suddenrun.app.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunnerService implements AppUserService {


  private final RunnerRepository repository;

  @Override
  public boolean isUserRegistered(String userId) {
    try {
      return repository.existsById(userId);
    } catch (RuntimeException e) {
      throw new GetSuddenrunUserRegistrationStatusException(userId, e);
    }
  }

  @Override
  public Optional<AppUser> getUser(String userId) {
    try {
      log.info("Getting app user with id [" + userId + "]");
      Optional<Runner> optionalRunner = repository.findById(userId);
      if (optionalRunner.isEmpty()) {
        log.info("User with id [" + userId + "] not found in app. Returning empty result.");
        return Optional.empty();
      }
      log.info("Found user with id [" + userId + "] in app. Returning user.");
      return optionalRunner.map(AppUser.class::cast);
    } catch (RuntimeException e) {
      throw new GetSuddenrunUserException(userId, e);
    }
  }

  @Override
  public AppUser registerUser(String userId, String userName) {
    try {
      log.info("Registering user with id [" + userId + "] in app");
      Runner runner = Runner.builder().id(userId).name(userName).build();
      repository.save(runner);
      log.info("Registered user with id [" + userId + "] in app");
      return runner;
    } catch (RuntimeException e) {
      throw new RegisterSuddenrunUserException(userId, e);
    }
  }
}

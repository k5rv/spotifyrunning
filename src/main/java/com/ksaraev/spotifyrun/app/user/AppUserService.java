package com.ksaraev.spotifyrun.app.user;

import java.util.Optional;

public interface AppUserService {

  boolean isUserRegistered(String userId);

  Optional<AppUser> getUser(String userId);

  AppUser registerUser(String userId, String userName);

  Optional<AppUser> getAuthenticatedUser();
}

package com.ksaraev.spotifyrun.app.user;

public interface AppUserService {
  boolean isUserExists();

  AppUser createUser();

  AppUser getUser();
}

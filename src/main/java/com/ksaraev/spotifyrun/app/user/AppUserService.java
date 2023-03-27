package com.ksaraev.spotifyrun.app.user;

public interface AppUserService {
  boolean isUserExists();

  boolean isUserExists(String id);

  AppUser createUser();

  AppUser getUser();

  boolean hasPlaylist(AppUser appUser);
AppUser getUser(String id);AppUser createUser(String id, String name);}

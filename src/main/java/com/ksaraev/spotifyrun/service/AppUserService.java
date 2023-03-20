package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;

public interface AppUserService {

  boolean hasPlaylist(SpotifyUser user);

  SpotifyPlaylist createPlaylist(SpotifyUser spotifyUser);

  boolean isUserRegistered(String id);

  void registerUser(SpotifyUser spotifyUser);

  SpotifyUser getAuthenticatedUser();
}

package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.user.SpotifyUser;

public interface SpotifyUserService {
  SpotifyUser getCurrentUser();
  SpotifyUser getUser(String id);
}

package com.ksaraev.spotifyrun.app.runner;

import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;

public interface SpotifyAppUserService {
  boolean isUserRegistered(SpotifyUserProfileItem userProfileItem);

  void registerUser(SpotifyUserProfileItem userProfileItem);
}

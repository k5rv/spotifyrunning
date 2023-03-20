package com.ksaraev.spotifyrun.model.spotify;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrun.model.user.User;

@JsonDeserialize(as = User.class)
public interface SpotifyUser extends SpotifyItem {

  String getEmail();

  void setEmail(String email);
}

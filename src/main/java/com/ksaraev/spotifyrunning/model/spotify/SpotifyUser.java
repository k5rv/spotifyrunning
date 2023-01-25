package com.ksaraev.spotifyrunning.model.spotify;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.model.user.User;

@JsonDeserialize(as = User.class)
public interface SpotifyUser extends SpotifyEntity {
  String getEmail();

  void setEmail(String email);
}

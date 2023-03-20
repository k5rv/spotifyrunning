package com.ksaraev.spotifyrun.model.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrun.model.spotify.SpotifyItem;

@JsonDeserialize(as = AppUser.class)
public interface SpotifyUser extends SpotifyItem {

  String getEmail();

  void setEmail(String email);
}

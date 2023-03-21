package com.ksaraev.spotifyrun.model.spotify.userprofile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrun.model.SpotifyItem;

@JsonDeserialize(as = SpotifyUserProfile.class)
public interface SpotifyUserProfileItem extends SpotifyItem {

  String getEmail();

  void setEmail(String email);
}

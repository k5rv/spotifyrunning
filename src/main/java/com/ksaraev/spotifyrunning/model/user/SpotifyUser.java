package com.ksaraev.spotifyrunning.model.user;

import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;

import javax.validation.constraints.Email;

public interface SpotifyUser extends SpotifyEntity {

  @Email
  String getEmail();

  void setEmail(String email);
}

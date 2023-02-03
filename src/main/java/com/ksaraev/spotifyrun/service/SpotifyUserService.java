package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.Valid;

import java.util.List;

public interface SpotifyUserService {

  @Valid
  SpotifyUser getUser();

  List<@Valid SpotifyTrack> getTopTracks();
}

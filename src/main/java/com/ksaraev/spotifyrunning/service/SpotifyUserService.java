package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
import jakarta.validation.Valid;

import java.util.List;

public interface SpotifyUserService {

  @Valid
  SpotifyUser getUser();

  List<@Valid SpotifyTrack> getTopTracks();
}

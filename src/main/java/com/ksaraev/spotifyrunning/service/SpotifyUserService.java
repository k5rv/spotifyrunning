package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;

import java.util.List;

public interface SpotifyUserService {

  SpotifyUser getUser();

  List<SpotifyTrack> getTopTracks();
}

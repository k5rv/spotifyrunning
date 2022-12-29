package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;

import java.util.List;

public interface SpotifyUserService {

  SpotifyUser getUser();

  List<SpotifyTrack> getTopTracks();
}

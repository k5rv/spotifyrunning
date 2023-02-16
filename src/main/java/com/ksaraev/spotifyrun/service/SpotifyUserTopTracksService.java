package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;

import java.util.List;

public interface SpotifyUserTopTracksService {

  List<SpotifyTrack> getUserTopTracks();
}

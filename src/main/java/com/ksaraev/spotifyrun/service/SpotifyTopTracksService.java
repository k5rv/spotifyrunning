package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;

import java.util.List;

public interface SpotifyTopTracksService {

  List<SpotifyTrack> getTopTracks();
}

package com.ksaraev.spotifyrunning.model.spotify;

import java.util.List;

public interface SpotifyTrack extends SpotifyItem {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtist> getArtists();

  void setArtists(List<SpotifyArtist> artists);
}

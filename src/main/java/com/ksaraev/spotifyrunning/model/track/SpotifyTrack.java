package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;

import java.util.List;

public interface SpotifyTrack extends SpotifyEntity {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtist> getArtists();

  void setArtists(List<SpotifyArtist> artists);
}

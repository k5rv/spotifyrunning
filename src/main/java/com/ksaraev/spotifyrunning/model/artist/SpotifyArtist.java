package com.ksaraev.spotifyrunning.model.artist;

import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;

import java.util.List;

public interface SpotifyArtist extends SpotifyEntity {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

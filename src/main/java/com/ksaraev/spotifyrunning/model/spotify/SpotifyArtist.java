package com.ksaraev.spotifyrunning.model.spotify;

import java.util.List;

public interface SpotifyArtist extends SpotifyEntity {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

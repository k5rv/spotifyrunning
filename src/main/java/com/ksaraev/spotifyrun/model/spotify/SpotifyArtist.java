package com.ksaraev.spotifyrun.model.spotify;

import java.util.List;

public interface SpotifyArtist extends SpotifyItem {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

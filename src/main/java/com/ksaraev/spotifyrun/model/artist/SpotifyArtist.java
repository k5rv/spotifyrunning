package com.ksaraev.spotifyrun.model.artist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyItem;
import java.util.List;

public interface SpotifyArtist extends SpotifyItem {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

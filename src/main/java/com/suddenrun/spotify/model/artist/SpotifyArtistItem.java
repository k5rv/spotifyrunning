package com.suddenrun.spotify.model.artist;

import com.suddenrun.spotify.model.SpotifyItem;
import java.util.List;

public interface SpotifyArtistItem extends SpotifyItem {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

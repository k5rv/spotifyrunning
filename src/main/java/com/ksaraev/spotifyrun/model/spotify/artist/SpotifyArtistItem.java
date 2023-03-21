package com.ksaraev.spotifyrun.model.spotify.artist;

import com.ksaraev.spotifyrun.model.SpotifyItem;
import java.util.List;

public interface SpotifyArtistItem extends SpotifyItem {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

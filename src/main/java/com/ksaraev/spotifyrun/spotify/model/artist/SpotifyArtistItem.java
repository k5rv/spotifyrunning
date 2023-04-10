package com.ksaraev.spotifyrun.spotify.model.artist;

import com.ksaraev.spotifyrun.spotify.model.SpotifyItem;
import java.util.List;

public interface SpotifyArtistItem extends SpotifyItem {

  List<String> getGenres();

  void setGenres(List<String> genres);
}

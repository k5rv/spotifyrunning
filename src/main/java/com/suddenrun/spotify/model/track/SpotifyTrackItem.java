package com.suddenrun.spotify.model.track;

import com.suddenrun.spotify.model.artist.SpotifyArtistItem;
import com.suddenrun.spotify.model.SpotifyItem;

import java.util.List;

public interface SpotifyTrackItem extends SpotifyItem {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtistItem> getArtists();

  void setArtists(List<SpotifyArtistItem> artists);
}

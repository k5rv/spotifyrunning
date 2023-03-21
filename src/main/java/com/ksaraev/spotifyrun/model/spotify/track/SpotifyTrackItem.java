package com.ksaraev.spotifyrun.model.spotify.track;

import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtistItem;
import com.ksaraev.spotifyrun.model.SpotifyItem;
import java.util.List;

public interface SpotifyTrackItem extends SpotifyItem {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtistItem> getArtists();

  void setArtists(List<SpotifyArtistItem> artists);
}

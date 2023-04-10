package com.ksaraev.spotifyrun.spotify.model.track;

import com.ksaraev.spotifyrun.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.spotifyrun.spotify.model.SpotifyItem;
import java.util.List;

public interface SpotifyTrackItem extends SpotifyItem {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtistItem> getArtists();

  void setArtists(List<SpotifyArtistItem> artists);
}

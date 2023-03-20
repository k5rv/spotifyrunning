package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyItem;
import java.util.List;

public interface SpotifyTrack extends SpotifyItem {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtist> getArtists();

  void setArtists(List<SpotifyArtist> artists);
}

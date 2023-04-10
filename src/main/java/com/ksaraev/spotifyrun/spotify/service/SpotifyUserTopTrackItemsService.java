package com.ksaraev.spotifyrun.spotify.service;

import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import java.util.List;

public interface SpotifyUserTopTrackItemsService {

  List<SpotifyTrackItem> getUserTopTracks();
}

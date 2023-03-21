package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import java.util.List;

public interface SpotifyUserTopTrackItemsService {

  List<SpotifyTrackItem> getUserTopTracks();
}

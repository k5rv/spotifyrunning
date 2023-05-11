package com.suddenrun.spotify.service;

import com.suddenrun.spotify.model.track.SpotifyTrackItem;

import java.util.List;

public interface SpotifyUserTopTrackItemsService {

  List<SpotifyTrackItem> getUserTopTracks();
}

package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;

import javax.validation.constraints.NotNull;

public interface SpotifyRunningPlaylistService {

  SpotifyPlaylist createPlaylist(
      @NotNull SpotifyPlaylistDetails playlistDetails,
      SpotifyRecommendationsFeatures recommendationsFeatures);
}

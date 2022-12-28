package com.ksaraev.spotifyrunning.config.playlist;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;

public interface SpotifyRunningPlaylistConfig {

  SpotifyPlaylistDetails getSpotifyPlaylistDetails();

  SpotifyRecommendationFeatures getSpotifyRecommendationFeatures();
}

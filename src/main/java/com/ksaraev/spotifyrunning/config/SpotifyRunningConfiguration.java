package com.ksaraev.spotifyrunning.config;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;

public interface SpotifyRunningConfiguration {

  SpotifyPlaylistDetails spotifyPlaylistDetails();

  SpotifyRecommendationFeatures spotifyRecommendationFeatures();
}

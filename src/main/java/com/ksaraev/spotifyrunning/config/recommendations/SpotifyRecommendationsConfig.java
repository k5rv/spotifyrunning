package com.ksaraev.spotifyrunning.config.recommendations;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;

public interface SpotifyRecommendationsConfig {

  SpotifyPlaylistDetails getSpotifyPlaylistDetails();

  SpotifyRecommendationFeatures getSpotifyRecommendationFeatures();

  Integer getRecommendationItemsRequestLimit();
}

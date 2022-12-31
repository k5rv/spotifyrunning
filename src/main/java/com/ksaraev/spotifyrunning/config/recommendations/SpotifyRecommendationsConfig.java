package com.ksaraev.spotifyrunning.config.recommendations;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;

public interface SpotifyRecommendationsConfig {

  SpotifyPlaylistDetails getSpotifyPlaylistDetails();

  SpotifyRecommendationsFeatures getSpotifyRecommendationFeatures();

  Integer getRecommendationItemsRequestLimit();
}

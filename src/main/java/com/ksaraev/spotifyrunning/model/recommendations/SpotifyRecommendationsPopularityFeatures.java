package com.ksaraev.spotifyrunning.model.recommendations;

public interface SpotifyRecommendationsPopularityFeatures extends SpotifyPopularity {

  Integer getMaxPopularity();

  Integer getMinPopularity();
}

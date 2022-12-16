package com.ksaraev.spotifyrunning.client.dto.recommendation;

import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPopularity;

public interface SpotifyRecommendationPopularityFeatures extends SpotifyPopularity {

  Integer getMaxPopularity();

  Integer getMinPopularity();
}

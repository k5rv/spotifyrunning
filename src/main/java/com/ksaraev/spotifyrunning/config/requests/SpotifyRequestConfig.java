package com.ksaraev.spotifyrunning.config.requests;

public interface SpotifyRequestConfig {

  Integer getUserTopItemsRequestLimit();

  Integer getRecommendationItemsRequestLimit();

  String getUserTopItemsRequestTimeRange();
}

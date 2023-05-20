package com.ksaraev.spotify.config;

public interface GetSpotifyUserTopItemsRequestConfig {

  Integer getLimit();

  Integer getOffset();

  String getTimeRange();
}

package com.suddenrun.spotify.config;

public interface GetSpotifyUserTopItemsRequestConfig {

  Integer getLimit();

  Integer getOffset();

  String getTimeRange();
}

package com.ksaraev.spotifyrun.spotify.config;

public interface GetSpotifyUserTopItemsRequestConfig {

  Integer getLimit();

  Integer getOffset();

  String getTimeRange();
}

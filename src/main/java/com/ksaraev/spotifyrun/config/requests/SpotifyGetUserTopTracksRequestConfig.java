package com.ksaraev.spotifyrun.config.requests;

public interface SpotifyGetUserTopTracksRequestConfig {

  Integer getLimit();

  Integer getOffset();

  String getTimeRange();
}

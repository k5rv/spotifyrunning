package com.ksaraev.spotifyrunning.config.runningplaylist;

import org.springframework.beans.factory.annotation.Value;

public class RunningPlaylistConfig implements SpotifyRunningPlaylistConfig {

  @Value("${app.limits.playlist-size}")
  private Integer playlistSizeLimit;

  @Override
  public Integer getPlaylistSizeLimit() {
    return this.playlistSizeLimit;
  }
}

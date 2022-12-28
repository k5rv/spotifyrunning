package com.ksaraev.spotifyrunning.config.app;

import com.ksaraev.spotifyrunning.config.playlist.RunningPlaylistConfig;
import com.ksaraev.spotifyrunning.config.playlist.SpotifyRunningPlaylistConfig;
import com.ksaraev.spotifyrunning.config.requests.RequestConfig;
import com.ksaraev.spotifyrunning.config.requests.SpotifyRequestConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Qualifier("SpotifyRunningApplicationConfigProduction")
public class SpotifyRunningApplicationConfig {

  @Bean
  SpotifyRunningPlaylistConfig runningPlaylistConfig() {
    return new RunningPlaylistConfig();
  }

  @Bean
  SpotifyRequestConfig requestConfig() {
    return new RequestConfig();
  }
}

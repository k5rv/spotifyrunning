package com.ksaraev.spotifyrunning.config;

import com.ksaraev.spotifyrunning.config.recommendations.RecommendationsConfig;
import com.ksaraev.spotifyrunning.config.recommendations.SpotifyRecommendationsConfig;
import com.ksaraev.spotifyrunning.config.runningplaylist.RunningPlaylistConfig;
import com.ksaraev.spotifyrunning.config.runningplaylist.SpotifyRunningPlaylistConfig;
import com.ksaraev.spotifyrunning.config.topitems.SpotifyUserTopItemsConfig;
import com.ksaraev.spotifyrunning.config.topitems.UserTopItemsConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Qualifier("SpotifyRunningApplicationConfigProduction")
public class SpotifyRunningApplicationConfig {

  @Bean
  SpotifyRecommendationsConfig recommendationsConfig() {
    return new RecommendationsConfig();
  }

  @Bean
  SpotifyUserTopItemsConfig userTopItemsConfig() {
    return new UserTopItemsConfig();
  }

  @Bean
  SpotifyRunningPlaylistConfig runningPlaylistConfig() {
    return new RunningPlaylistConfig();
  }
}

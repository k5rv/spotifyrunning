package com.ksaraev.spotifyrunning.config;

import com.ksaraev.spotifyrunning.config.playlist.RunningWorkoutPlaylistConfig;
import com.ksaraev.spotifyrunning.config.playlist.SpotifyRunningWorkoutPlaylistConfig;
import com.ksaraev.spotifyrunning.config.requests.GetRecommendationsRequestConfig;
import com.ksaraev.spotifyrunning.config.requests.GetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrunning.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrunning.config.requests.SpotifyGetUserTopTracksRequestConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Qualifier("SpotifyRunningConfigProduction")
public class SpotifyRunningConfig {

  @Bean
  SpotifyRunningWorkoutPlaylistConfig runningWorkoutPlaylistConfig() {
    return new RunningWorkoutPlaylistConfig();
  }

  @Bean
  SpotifyGetUserTopTracksRequestConfig getUserTopTracksRequestConfig() {
    return new GetUserTopTracksRequestConfig();
  }

  @Bean
  SpotifyGetRecommendationsRequestConfig getRecommendationsRequestConfig() {
    return new GetRecommendationsRequestConfig();
  }
}

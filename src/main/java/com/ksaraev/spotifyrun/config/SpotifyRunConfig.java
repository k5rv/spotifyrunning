package com.ksaraev.spotifyrun.config;

import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.config.playlist.WorkoutPlaylistConfig;
import com.ksaraev.spotifyrun.config.requests.GetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.config.requests.GetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Qualifier("SpotifyRunConfigProduction")
public class SpotifyRunConfig {

  @Bean
  SpotifyRunPlaylistConfig getWorkoutPlaylistConfig() {
    return new WorkoutPlaylistConfig();
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

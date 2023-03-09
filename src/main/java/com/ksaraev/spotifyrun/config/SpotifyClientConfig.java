package com.ksaraev.spotifyrun.config;

import com.ksaraev.spotifyrun.config.requests.GetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.config.requests.GetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyClientConfig {

  @Value("${spotifyrun.client.requests.get-user-top-tracks.limit}")
  private Integer getUserTopTracksRequestLimit;

  @Value("${spotifyrun.client.requests.get-user-top-tracks.offset}")
  private Integer getUserTopTracksRequestOffset;

  @Value("${spotifyrun.client.requests.get-user-top-tracks.time-range}")
  private String getUserTopTracksRequestTimeRange;

  @Value("${spotifyrun.client.requests.get-recommendations.limit}")
  private Integer getRecommendationsRequestLimit;

  @Bean
  SpotifyGetUserTopTracksRequestConfig getUserTopTracksRequestConfig() {
    return GetUserTopTracksRequestConfig.builder()
        .timeRange(this.getUserTopTracksRequestTimeRange)
        .limit(this.getUserTopTracksRequestLimit)
        .offset(this.getUserTopTracksRequestOffset)
        .build();
  }

  @Bean
  SpotifyGetRecommendationsRequestConfig getRecommendationsRequestConfig() {
    return GetRecommendationsRequestConfig.builder()
        .limit(this.getRecommendationsRequestLimit)
        .build();
  }
}

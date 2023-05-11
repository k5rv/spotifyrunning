package com.ksaraev.spotifyrun.spotify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyClientConfig {

  @Value("${suddenrun.client.requests.get-user-top-tracks.limit}")
  private Integer getUserTopTracksRequestLimit;

  @Value("${suddenrun.client.requests.get-user-top-tracks.offset}")
  private Integer getUserTopTracksRequestOffset;

  @Value("${suddenrun.client.requests.get-user-top-tracks.time-range}")
  private String getUserTopTracksRequestTimeRange;

  @Value("${suddenrun.client.requests.get-recommendations.limit}")
  private Integer getRecommendationsRequestLimit;

  @Value("${suddenrun.client.requests.update-playlist-items-request.position}")
  private Integer updatePlaylistItemsRequestPosition;

  @Bean
  GetSpotifyUserTopItemsRequestConfig getUserTopTracksRequestConfig() {
    return GetSpotifyUserTopTracksRequestConfig.builder()
        .timeRange(this.getUserTopTracksRequestTimeRange)
        .limit(this.getUserTopTracksRequestLimit)
        .offset(this.getUserTopTracksRequestOffset)
        .build();
  }

  @Bean
  GetSpotifyRecommendationItemsRequestConfig getRecommendationsRequestConfig() {
    return GetSpotifyRecommendationsRequestConfig.builder()
        .limit(this.getRecommendationsRequestLimit)
        .build();
  }

  @Bean
  AddSpotifyPlaylistItemsRequestConfig getUpdatePlaylistRequestConfig() {
    return AddSpotifyPlaylistTracksRequestConfig.builder()
        .position(this.updatePlaylistItemsRequestPosition)
        .build();
  }
}

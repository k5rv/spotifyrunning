package com.ksaraev.spotifyrunning.config;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.recommendation.RecommendationFeatures;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@Qualifier("SpotifyRunningApplicationConfigProduction")
public class SpotifyRunningApplicationConfig {

  @Value("${spotifyrunning.playlist.name}")
  private String name;

  @Value("${spotifyrunning.playlist.description}")
  private String description;

  @Value("${spotifyrunning.recommendation-features.min-tempo}")
  private BigDecimal minTempo;

  @Value("${spotifyrunning.recommendation-features.max-tempo}")
  private BigDecimal maxTempo;

  @Value("${spotifyrunning.recommendation-features.min-energy}")
  private BigDecimal minEnergy;

  @Bean
  SpotifyRunningPlaylistConfig runningPlaylistConfig() {
    SpotifyPlaylistDetails playlistDetails =
        PlaylistDetails.builder()
            .isCollaborative(false)
            .isPublic(false)
            .name(this.name)
            .description(this.description)
            .build();

    SpotifyRecommendationFeatures recommendationFeatures =
        RecommendationFeatures.builder()
            .minEnergy(BigDecimal.valueOf(0.65))
            .minTempo(BigDecimal.valueOf(185.00))
            .maxTempo(BigDecimal.valueOf(205.00))
            .build();

    return new RunningPlaylistConfig(playlistDetails, recommendationFeatures);
  }
}

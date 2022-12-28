package com.ksaraev.spotifyrunning.config.playlist;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.recommendation.RecommendationFeatures;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RunningPlaylistConfig implements SpotifyRunningPlaylistConfig {

  @Value("${app.playlist.name}")
  private String name;

  @Value("${app.playlist.description}")
  private String description;

  @Value("${app.playlist.features.min-tempo}")
  private BigDecimal minTempo;

  @Value("${app.playlist.features.max-tempo}")
  private BigDecimal maxTempo;

  @Value("${app.playlist.features.min-energy}")
  private BigDecimal minEnergy;

  @Override
  public SpotifyPlaylistDetails getSpotifyPlaylistDetails() {
    return PlaylistDetails.builder()
        .isCollaborative(false)
        .isPublic(false)
        .name(this.name)
        .description(this.description)
        .build();
  }

  @Override
  public SpotifyRecommendationFeatures getSpotifyRecommendationFeatures() {
    return RecommendationFeatures.builder()
        .minEnergy(BigDecimal.valueOf(0.65))
        .minTempo(BigDecimal.valueOf(185.00))
        .maxTempo(BigDecimal.valueOf(205.00))
        .build();
  }
}

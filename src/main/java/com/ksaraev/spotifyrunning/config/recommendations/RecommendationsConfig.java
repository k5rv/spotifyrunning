package com.ksaraev.spotifyrunning.config.recommendations;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.playlist.details.PlaylistDetails;
import com.ksaraev.spotifyrunning.model.recommendations.RecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RecommendationsConfig implements SpotifyRecommendationsConfig {

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

  @Value("${app.requests.get-recommendations.limit}")
  private Integer recommendationsRequestLimit;

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
  public SpotifyRecommendationsFeatures getSpotifyRecommendationFeatures() {
    return RecommendationsFeatures.builder()
        .minEnergy(this.minEnergy)
        .minTempo(this.minTempo)
        .maxTempo(this.maxTempo)
        .build();
  }

  @Override
  public Integer getRecommendationItemsRequestLimit() {
    return this.recommendationsRequestLimit;
  }
}

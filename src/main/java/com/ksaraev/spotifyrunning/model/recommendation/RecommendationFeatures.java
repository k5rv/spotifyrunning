package com.ksaraev.spotifyrunning.model.recommendation;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFeatures implements SpotifyRecommendationFeatures {
  private BigDecimal maxTempo;
  private BigDecimal minTempo;
  private BigDecimal tempo;
  private BigDecimal maxAcousticness;
  private BigDecimal minAcousticness;
  private BigDecimal acousticness;
  private BigDecimal maxDanceability;
  private BigDecimal minDanceability;
  private BigDecimal danceability;
  private Integer maxDurationMs;
  private Integer minDurationMs;
  private Integer durationMs;
  private BigDecimal maxInstrumentalness;
  private BigDecimal minInstrumentalness;
  private BigDecimal instrumentalness;
  private Integer maxKey;
  private Integer minKey;
  private Integer key;
  private BigDecimal maxEnergy;
  private BigDecimal minEnergy;
  private BigDecimal energy;
  private BigDecimal maxLiveness;
  private BigDecimal minLiveness;
  private BigDecimal liveness;
  private BigDecimal minLoudness;
  private BigDecimal maxLoudness;
  private BigDecimal loudness;
  private Integer maxMode;
  private Integer minMode;
  private Integer mode;
  private Integer maxPopularity;
  private Integer minPopularity;
  private Integer popularity;
  private BigDecimal maxSpeechiness;
  private BigDecimal minSpeechiness;
  private BigDecimal Speechiness;
  private Integer maxTimeSignature;
  private Integer minTimeSignature;
  private Integer timeSignature;
  private BigDecimal maxValence;
  private BigDecimal minValence;
  private BigDecimal valence;
}

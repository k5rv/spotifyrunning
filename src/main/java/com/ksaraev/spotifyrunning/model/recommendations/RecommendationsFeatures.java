package com.ksaraev.spotifyrunning.model.recommendations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationsFeatures implements SpotifyRecommendationsFeatures {

  private Integer maxPopularity;

  private Integer minPopularity;

  private Integer popularity;

  private Integer maxKey;

  private Integer minKey;

  private Integer key;

  private Integer maxMode;

  private Integer minMode;

  private Integer mode;

  private Integer maxDurationMs;

  private Integer minDurationMs;

  private Integer durationMs;

  private Integer maxTimeSignature;

  private Integer minTimeSignature;

  private Integer timeSignature;

  private BigDecimal minTempo;

  private BigDecimal maxTempo;

  private BigDecimal tempo;

  private BigDecimal minEnergy;

  private BigDecimal maxEnergy;

  private BigDecimal energy;

  private BigDecimal minAcousticness;

  private BigDecimal maxAcousticness;

  private BigDecimal acousticness;

  private BigDecimal minDanceability;

  private BigDecimal maxDanceability;

  private BigDecimal danceability;

  private BigDecimal maxInstrumentalness;

  private BigDecimal minInstrumentalness;

  private BigDecimal instrumentalness;

  private BigDecimal maxLiveness;

  private BigDecimal minLiveness;

  private BigDecimal liveness;

  private BigDecimal minLoudness;

  private BigDecimal maxLoudness;

  private BigDecimal loudness;

  private BigDecimal maxSpeechiness;

  private BigDecimal minSpeechiness;

  private BigDecimal speechiness;

  private BigDecimal maxValence;

  private BigDecimal minValence;

  private BigDecimal valence;
}

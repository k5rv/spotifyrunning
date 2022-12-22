package com.ksaraev.spotifyrunning.model.recommendation;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import feign.Param;
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

  @Param("max_popularity")
  private Integer maxPopularity;

  @Param("min_popularity")
  private Integer minPopularity;

  @Param("target_popularity")
  private Integer popularity;

  @Param("max_key")
  private Integer maxKey;

  @Param("min_key")
  private Integer minKey;

  @Param("target_key")
  private Integer key;

  @Param("max_mode")
  private Integer maxMode;

  @Param("min_mode")
  private Integer minMode;

  @Param("target_mode")
  private Integer mode;

  @Param("max_duration_ms")
  private Integer maxDurationMs;

  @Param("min_duration_ms")
  private Integer minDurationMs;

  @Param("target_duration_ms")
  private Integer durationMs;

  @Param("max_time_signature")
  private Integer maxTimeSignature;

  @Param("min_time_signature")
  private Integer minTimeSignature;

  @Param("target_time_signature")
  private Integer timeSignature;

  @Param("min_tempo")
  private BigDecimal minTempo;

  @Param("max_tempo")
  private BigDecimal maxTempo;

  @Param("target_tempo")
  private BigDecimal tempo;

  @Param("min_energy")
  private BigDecimal minEnergy;

  @Param("max_energy")
  private BigDecimal maxEnergy;

  @Param("target_energy")
  private BigDecimal energy;

  @Param("min_acousticness")
  private BigDecimal minAcousticness;

  @Param("max_acousticness")
  private BigDecimal maxAcousticness;

  @Param("target_acousticness")
  private BigDecimal acousticness;

  @Param("min_danceability")
  private BigDecimal minDanceability;

  @Param("max_danceability")
  private BigDecimal maxDanceability;

  @Param("target_danceability")
  private BigDecimal danceability;

  @Param("max_instrumentalness")
  private BigDecimal maxInstrumentalness;

  @Param("min_instrumentalness")
  private BigDecimal minInstrumentalness;

  @Param("target_instrumentalness")
  private BigDecimal instrumentalness;

  @Param("max_instrumentalness")
  private BigDecimal maxLiveness;

  @Param("min_instrumentalness")
  private BigDecimal minLiveness;

  @Param("target_instrumentalness")
  private BigDecimal liveness;

  @Param("min_loudness")
  private BigDecimal minLoudness;

  @Param("max_loudness")
  private BigDecimal maxLoudness;

  @Param("target_loudness")
  private BigDecimal loudness;

  @Param("max_speechiness")
  private BigDecimal maxSpeechiness;

  @Param("min_speechiness")
  private BigDecimal minSpeechiness;

  @Param("target_speechiness")
  private BigDecimal speechiness;

  @Param("max_valence")
  private BigDecimal maxValence;

  @Param("min_valence")
  private BigDecimal minValence;

  @Param("target_valence")
  private BigDecimal valence;
}

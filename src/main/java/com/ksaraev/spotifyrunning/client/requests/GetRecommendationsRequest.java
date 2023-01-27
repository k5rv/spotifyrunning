package com.ksaraev.spotifyrunning.client.requests;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record GetRecommendationsRequest(
    List<String> seedArtists,
    List<String> seedTracks,
    List<String> seedGenres,
    TrackFeatures trackFeatures,
    Integer limit,
    Integer offset) {
  @Builder
  public GetRecommendationsRequest {}

  public record TrackFeatures(
      Integer maxPopularity,
      Integer minPopularity,
      Integer popularity,
      Integer maxKey,
      Integer minKey,
      Integer key,
      Integer maxMode,
      Integer minMode,
      Integer mode,
      Integer maxDurationMs,
      Integer minDurationMs,
      Integer durationMs,
      Integer maxTimeSignature,
      Integer minTimeSignature,
      Integer timeSignature,
      BigDecimal minTempo,
      BigDecimal maxTempo,
      BigDecimal tempo,
      BigDecimal minEnergy,
      BigDecimal maxEnergy,
      BigDecimal energy,
      BigDecimal minAcousticness,
      BigDecimal maxAcousticness,
      BigDecimal acousticness,
      BigDecimal minDanceability,
      BigDecimal maxDanceability,
      BigDecimal danceability,
      BigDecimal maxInstrumentalness,
      BigDecimal minInstrumentalness,
      BigDecimal instrumentalness,
      BigDecimal maxLiveness,
      BigDecimal minLiveness,
      BigDecimal liveness,
      BigDecimal minLoudness,
      BigDecimal maxLoudness,
      BigDecimal loudness,
      BigDecimal maxSpeechiness,
      BigDecimal minSpeechiness,
      BigDecimal speechiness,
      BigDecimal maxValence,
      BigDecimal minValence,
      BigDecimal valence) {

    @Builder
    public TrackFeatures {}
  }
}

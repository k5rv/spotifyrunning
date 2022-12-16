package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import feign.Param;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class GetRecommendationItemsRequest
    implements GetSpotifyUserItemsRequest, SpotifyRecommendationFeatures {

  @NotNull
  @Size(min = 1, max = 5)
  @Param("seed_artists")
  List<String> seedArtists;

  @NotNull
  @Size(min = 1, max = 5)
  @Param("seed_tracks")
  List<String> seedTracks;

  @NotNull
  @Size(min = 1, max = 5)
  @Param("seed_genres")
  List<String> seedGenres;

  @Min(0)
  @Max(50)
  Integer limit;

  @Min(0)
  Integer offset;

  @Param("max_popularity")
  Integer maxPopularity;

  @Param("min_popularity")
  Integer minPopularity;

  @Param("target_popularity")
  Integer popularity;

  @Param("max_key")
  Integer maxKey;

  @Param("min_key")
  Integer minKey;

  @Param("target_key")
  Integer key;

  @Param("max_mode")
  Integer maxMode;

  @Param("min_mode")
  Integer minMode;

  @Param("target_mode")
  Integer mode;

  @Param("max_duration_ms")
  Integer maxDurationMs;

  @Param("min_duration_ms")
  Integer minDurationMs;

  @Param("target_duration_ms")
  Integer durationMs;

  @Param("max_time_signature")
  Integer maxTimeSignature;

  @Param("min_time_signature")
  Integer minTimeSignature;

  @Param("target_time_signature")
  Integer timeSignature;

  @Param("min_tempo")
  BigDecimal minTempo;

  @Param("max_tempo")
  BigDecimal maxTempo;

  @Param("target_tempo")
  BigDecimal tempo;

  @Param("min_energy")
  BigDecimal minEnergy;

  @Param("max_energy")
  BigDecimal maxEnergy;

  @Param("target_energy")
  BigDecimal energy;

  @Param("min_acousticness")
  BigDecimal minAcousticness;

  @Param("max_acousticness")
  BigDecimal maxAcousticness;

  @Param("target_acousticness")
  BigDecimal acousticness;

  @Param("min_danceability")
  BigDecimal minDanceability;

  @Param("max_danceability")
  BigDecimal maxDanceability;

  @Param("target_danceability")
  BigDecimal danceability;

  @Param("max_instrumentalness")
  BigDecimal maxInstrumentalness;

  @Param("min_instrumentalness")
  BigDecimal minInstrumentalness;

  @Param("target_instrumentalness")
  BigDecimal instrumentalness;

  @Param("max_instrumentalness")
  BigDecimal maxLiveness;

  @Param("min_instrumentalness")
  BigDecimal minLiveness;

  @Param("target_instrumentalness")
  BigDecimal liveness;

  @Param("min_loudness")
  BigDecimal minLoudness;

  @Param("max_loudness")
  BigDecimal maxLoudness;

  @Param("target_loudness")
  BigDecimal loudness;

  @Param("max_speechiness")
  BigDecimal maxSpeechiness;

  @Param("min_speechiness")
  BigDecimal minSpeechiness;

  @Param("target_speechiness")
  BigDecimal speechiness;

  @Param("max_valence")
  BigDecimal maxValence;

  @Param("min_valence")
  BigDecimal minValence;

  @Param("target_valence")
  BigDecimal valence;
}

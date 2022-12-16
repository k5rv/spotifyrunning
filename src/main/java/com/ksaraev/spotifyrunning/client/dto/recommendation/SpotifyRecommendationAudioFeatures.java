package com.ksaraev.spotifyrunning.client.dto.recommendation;

import com.ksaraev.spotifyrunning.client.dto.audiofeatures.SpotifyAudioFeatures;

import java.math.BigDecimal;

public interface SpotifyRecommendationAudioFeatures extends SpotifyAudioFeatures {

  BigDecimal getMaxAcousticness();

  BigDecimal getMaxEnergy();

  BigDecimal getMaxInstrumentalness();

  Integer getMaxKey();

  BigDecimal getMaxLiveness();

  BigDecimal getMaxLoudness();

  Integer getMaxMode();

  BigDecimal getMaxSpeechiness();

  BigDecimal getMaxTempo();

  BigDecimal getMaxValence();

  Integer getMaxTimeSignature();

  BigDecimal getMaxDanceability();

  Integer getMaxDurationMs();

  BigDecimal getMinAcousticness();

  BigDecimal getMinEnergy();

  BigDecimal getMinInstrumentalness();

  Integer getMinKey();

  BigDecimal getMinLiveness();

  BigDecimal getMinLoudness();

  Integer getMinMode();

  BigDecimal getMinSpeechiness();

  BigDecimal getMinTempo();

  BigDecimal getMinValence();

  Integer getMinTimeSignature();

  BigDecimal getMinDanceability();

  Integer getMinDurationMs();
}

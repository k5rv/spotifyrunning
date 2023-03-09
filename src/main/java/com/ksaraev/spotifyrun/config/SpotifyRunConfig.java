package com.ksaraev.spotifyrun.config;

import com.ksaraev.spotifyrun.config.playlist.PlaylistConfig;
import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyRunConfig {

  @Value("${spotifyrun.playlist.details.name}")
  @NotEmpty
  private String name;

  @Value("${spotifyrun.playlist.details.description}")
  @NotEmpty
  private String description;

  @Value("${spotifyrun.playlist.details.size}")
  @Min(1)
  @Max(50)
  private Integer size;

  @Value("${spotifyrun.playlist.features.min-tempo}")
  @Min(1)
  @Max(220)
  private BigDecimal minTempo;

  @Value("${spotifyrun.playlist.features.max-tempo}")
  @Min(1)
  @Max(220)
  private BigDecimal maxTempo;

  @Value("${spotifyrun.playlist.features.min-energy}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal minEnergy;

  @Bean
  SpotifyRunPlaylistConfig getWorkoutPlaylistConfig() {
    SpotifyPlaylistDetails playlistDetails =
        PlaylistDetails.builder().name(this.name).description(this.description).build();

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder()
            .minTempo(this.minTempo)
            .maxTempo(this.maxTempo)
            .minEnergy(this.minEnergy)
            .build();

    return PlaylistConfig.builder()
        .details(playlistDetails)
        .musicFeatures(trackFeatures)
        .size(size)
        .build();
  }
}

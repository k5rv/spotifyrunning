package com.ksaraev.spotifyrun.config;

import com.ksaraev.spotifyrun.config.playlist.PlaylistConfig;
import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.model.playlistdetails.PlaylistDetails;
import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.trackfeatures.TrackFeatures;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

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
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
    return jsonConverter;
  }

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


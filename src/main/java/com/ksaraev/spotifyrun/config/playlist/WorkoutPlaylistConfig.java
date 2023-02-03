package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Data
public class WorkoutPlaylistConfig implements SpotifyRunPlaylistConfig {

  @Value("${spotify-run.config.playlist.details.name}")
  private String name;

  @Value("${spotify-run.config.playlist.details.description}")
  private String description;

  @Value("${spotify-run.config.playlist.details.collaborative}")
  private Boolean isCollaborative;

  @Value("${spotify-run.config.playlist.details.public}")
  private Boolean isPublic;

  @Value("${spotify-run.config.playlist.details.size-limit}")
  private Integer sizeLimit;

  @Value("${spotify-run.config.playlist.features.min-tempo}")
  private BigDecimal minTempo;

  @Value("${spotify-run.config.playlist.features.max-tempo}")
  private BigDecimal maxTempo;

  @Value("${spotify-run.config.playlist.features.min-energy}")
  private BigDecimal minEnergy;

  @Override
  public SpotifyPlaylistDetails getDetails() {
    return PlaylistDetails.builder()
        .name(this.name)
        .description(this.description)
        .isCollaborative(false)
        .isPublic(false)
        .build();
  }

  @Override
  public SpotifyTrackFeatures getMusicFeatures() {
    return TrackFeatures.builder()
        .minTempo(this.minTempo)
        .maxTempo(this.maxTempo)
        .minEnergy(this.minEnergy)
        .build();
  }
}

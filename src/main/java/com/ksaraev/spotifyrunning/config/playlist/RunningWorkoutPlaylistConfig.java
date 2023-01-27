package com.ksaraev.spotifyrunning.config.playlist;

import com.ksaraev.spotifyrunning.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrunning.model.track.TrackFeatures;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Data
public class RunningWorkoutPlaylistConfig implements SpotifyRunningWorkoutPlaylistConfig {

  @Value("${spotify-running.config.playlist.details.name}")
  private String name;

  @Value("${spotify-running.config.playlist.details.description}")
  private String description;

  @Value("${spotify-running.config.playlist.details.collaborative}")
  private Boolean isCollaborative;

  @Value("${spotify-running.config.playlist.details.public}")
  private Boolean isPublic;

  @Value("${spotify-running.config.playlist.details.size-limit}")
  private Integer sizeLimit;

  @Value("${spotify-running.config.playlist.features.min-tempo}")
  private BigDecimal minTempo;

  @Value("${spotify-running.config.playlist.features.max-tempo}")
  private BigDecimal maxTempo;

  @Value("${spotify-running.config.playlist.features.min-energy}")
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

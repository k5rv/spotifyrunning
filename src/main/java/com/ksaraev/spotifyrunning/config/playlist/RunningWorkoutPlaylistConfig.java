package com.ksaraev.spotifyrunning.config.playlist;

import com.ksaraev.spotifyrunning.model.playlist.details.PlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrunning.model.track.TrackFeatures;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Data
public class RunningWorkoutPlaylistConfig implements SpotifyRunningWorkoutPlaylistConfig {

  @Value("${app.playlist.name}")
  private String name;

  @Value("${app.playlist.description}")
  private String description;

  @Value("${app.playlist.collaborative}")
  private Boolean isCollaborative;

  @Value("${app.playlist.public}")
  private Boolean isPublic;

  @Value("${app.limits.playlist-size}")
  private Integer sizeLimit;

  @Value("${app.playlist.features.min-tempo}")
  private BigDecimal minTempo;

  @Value("${app.playlist.features.max-tempo}")
  private BigDecimal maxTempo;

  @Value("${app.playlist.features.min-energy}")
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

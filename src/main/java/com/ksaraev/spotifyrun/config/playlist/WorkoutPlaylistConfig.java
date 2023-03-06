package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import java.math.BigDecimal;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class WorkoutPlaylistConfig implements SpotifyRunPlaylistConfig {

  @Value("${spotifyrun.config.playlist.details.name}")
  private String name;

  @Value("${spotifyrun.config.playlist.details.description}")
  private String description;

  @Value("${spotifyrun.config.playlist.details.collaborative}")
  private Boolean isCollaborative;

  @Value("${spotifyrun.config.playlist.details.public}")
  private Boolean isPublic;

  @Value("${spotifyrun.config.playlist.details.size-limit}")
  private Integer sizeLimit;

  @Value("${spotifyrun.config.playlist.features.min-tempo}")
  private BigDecimal minTempo;

  @Value("${spotifyrun.config.playlist.features.max-tempo}")
  private BigDecimal maxTempo;

  @Value("${spotifyrun.config.playlist.features.min-energy}")
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

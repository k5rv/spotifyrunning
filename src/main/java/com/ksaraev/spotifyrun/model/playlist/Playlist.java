package com.ksaraev.spotifyrun.model.playlist;

import com.ksaraev.spotifyrun.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.user.AppUser;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Playlist implements SpotifyPlaylist {
  @Id @NotNull private String id;
  @NotNull private String snapshotId;

  @ManyToOne(cascade = CascadeType.ALL)
  @Valid
  @NotNull
  private AppUser owner;

  @NotEmpty private String name;
   @NotNull private URI uri;
   private String description;
  private Boolean isPublic;
   private Boolean isCollaborative;
  @Transient @Valid private List<Track> tracks;

  @Override
  public void setOwner(SpotifyUser user) {
    this.owner = (AppUser) user;
  }

  @Override
  public SpotifyUser getOwner() {
    return this.owner;
  }

  @Override
  public void setTracks(List<SpotifyTrack> spotifyTracks) {
    this.tracks =
        spotifyTracks.stream()
            .flatMap(Stream::ofNullable)
            .filter(Objects::nonNull)
            .map(Track.class::cast)
            .toList();
  }

  @Override
  public List<SpotifyTrack> getTracks() {
    return this.tracks.stream()
        .flatMap(Stream::ofNullable)
        .filter(Objects::nonNull)
        .map(SpotifyTrack.class::cast)
        .toList();
  }
}

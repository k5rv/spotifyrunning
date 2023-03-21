package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.runner.Runner;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Playlist {

  @Id @GeneratedValue @UuidGenerator private UUID uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  private Runner runner;

  private String spotifyId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Playlist playlist = (Playlist) o;
    return getUuid() != null && Objects.equals(getUuid(), playlist.getUuid());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

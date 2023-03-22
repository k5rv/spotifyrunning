package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.user.Runner;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Playlist implements AppPlaylist {

  @Id @GeneratedValue @UuidGenerator private UUID uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  private Runner runner;

  private String spotifyId;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<String> trackIds = new ArrayList<>();

  @Override
  public String getExternalId() {
    return this.spotifyId;
  }

  @Override
  public void setExternalId(String id) {
    this.spotifyId = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Playlist playlist = (Playlist) o;
    return getUuid() != null && Objects.equals(getUuid(), playlist.getUuid());
  }

  public void addTrackIds(List<String> trackIds) {
    this.trackIds.addAll(trackIds);
  }

  public void removeTrackIds(List<String> trackIds) {
    this.trackIds.removeAll(trackIds);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

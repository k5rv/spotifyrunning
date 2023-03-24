package com.ksaraev.spotifyrun.app.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.Runner;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Playlist implements AppPlaylist {

  @Id private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Runner runner;

  @Type(JsonType.class)
  @JsonManagedReference
  @Column(columnDefinition = "jsonb")
  private List<String> trackIds = new ArrayList<>();

  @JsonIgnore
  @Override
  public AppUser getOwner() {
    return this.runner;
  }

  @JsonIgnore
  @Override
  public void setOwner(AppUser appUser) {
    this.runner = (Runner) appUser;
  }

  @Override
  public List<String> getTrackIds() {
    return this.trackIds;
  }

  @Override
  public void setTrackIds(List<String> tracks) {
    this.trackIds = tracks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Playlist playlist = (Playlist) o;
    return getId() != null && Objects.equals(getId(), playlist.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

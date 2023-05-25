package com.ksaraev.suddenrun.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SuddenrunPlaylist implements AppPlaylist {

  @Id
  @Column(nullable = false)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  private SuddenrunUser user;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<SuddenrunTrack> inclusions = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<SuddenrunTrack> exclusions = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<SuddenrunTrack> tracks = new ArrayList<>();

  private String snapshotId;

  public List<AppTrack> getInclusions() {
    return this.inclusions.stream().map(AppTrack.class::cast).collect(Collectors.toList());
  }

  public void setInclusions(List<AppTrack> inclusions) {
    this.inclusions =
        inclusions.stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
  }

  public List<AppTrack> getExclusions() {
    return this.exclusions.stream().map(AppTrack.class::cast).collect(Collectors.toList());
  }

  public void setExclusions(List<AppTrack> exclusions) {
    this.exclusions =
        exclusions.stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
  }

  @JsonIgnore
  @Override
  public AppUser getUser() {
    return this.user;
  }

  @JsonIgnore
  @Override
  public void setUser(AppUser appUser) {
    this.user = (SuddenrunUser) appUser;
  }

  @Override
  public List<AppTrack> getTracks() {
    return this.tracks.stream().map(AppTrack.class::cast).toList();
  }

  @Override
  public void setTracks(List<AppTrack> appTracks) {
    this.tracks = appTracks.stream().map(SuddenrunTrack.class::cast).toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    SuddenrunPlaylist playlist = (SuddenrunPlaylist) o;
    return getId() != null && Objects.equals(getId(), playlist.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

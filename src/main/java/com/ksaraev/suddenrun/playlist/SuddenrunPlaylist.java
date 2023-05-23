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
  private SuddenrunUser owner;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<SuddenrunTrack> addedByUser = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<SuddenrunTrack> removedByUser = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<SuddenrunTrack> tracks = new ArrayList<>();

  private String snapshotId;

  public List<AppTrack> getAddedByUser() {
    return this.addedByUser.stream().map(AppTrack.class::cast).toList();
  }

  public void setAddedByUser(List<AppTrack> addedByUser) {
    this.addedByUser = addedByUser.stream().map(SuddenrunTrack.class::cast).toList();
  }

  public List<AppTrack> getRemovedByUser() {
    return this.removedByUser.stream().map(AppTrack.class::cast).toList();
  }

  public void setRemovedByUser(List<AppTrack> removedByUser) {
    this.removedByUser = removedByUser.stream().map(SuddenrunTrack.class::cast).toList();
  }

  @JsonIgnore
  @Override
  public AppUser getOwner() {
    return this.owner;
  }

  @JsonIgnore
  @Override
  public void setOwner(AppUser appUser) {
    this.owner = (SuddenrunUser) appUser;
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

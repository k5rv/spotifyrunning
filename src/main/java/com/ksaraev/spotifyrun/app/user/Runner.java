package com.ksaraev.spotifyrun.app.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ksaraev.spotifyrun.app.playlist.AppPlaylist;
import com.ksaraev.spotifyrun.app.playlist.Playlist;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Runner implements AppUser {
  @Id private String id;

  private String name;

  @JsonBackReference
  @OneToMany(mappedBy = "runner", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Playlist> playlists = new ArrayList<>();

  public void addPlaylist(Playlist playlist) {
    playlists.add(playlist);
    playlist.setRunner(this);
  }

  public void removePlaylist(Playlist playlist) {
    playlists.remove(playlist);
    playlist.setRunner(null);
  }

  @Override
  public List<AppPlaylist> getPlaylists() {
    if (this.playlists == null) return List.of();
    return this.playlists.stream().map(AppPlaylist.class::cast).toList();
  }

  @Override
  public void setPlaylists(List<AppPlaylist> playlists) {
    if (playlists == null) {
      this.playlists = List.of();
      return;
    }
    this.playlists = playlists.stream().map(Playlist.class::cast).toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Runner runner = (Runner) o;
    return getId() != null && Objects.equals(getId(), runner.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

package com.ksaraev.suddenrun.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ksaraev.suddenrun.playlist.AppPlaylist;
import com.ksaraev.suddenrun.playlist.Playlist;
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
public class SuddenrunUser implements AppUser {
  @Id private String id;

  private String name;

  @JsonBackReference
  @OneToMany(mappedBy = "suddenrunUser", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Playlist> playlists = new ArrayList<>();

  @Override
  public void addPlaylist(AppPlaylist appPlaylist) {
    Playlist playlist = (Playlist) appPlaylist;
    playlists.add(playlist);
    playlist.setSuddenrunUser(this);
  }

  @Override
  public void removePlaylist(AppPlaylist appPlaylist) {
    Playlist playlist = (Playlist) appPlaylist;
    if (!this.playlists.isEmpty()) {
      playlists.remove(playlist);
    }
    playlist.setSuddenrunUser(null);
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
    SuddenrunUser suddenrunUser = (SuddenrunUser) o;
    return getId() != null && Objects.equals(getId(), suddenrunUser.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

package com.ksaraev.spotifyrun.app.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ksaraev.spotifyrun.app.playlist.AppPlaylist;
import com.ksaraev.spotifyrun.app.playlist.Playlist;
import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.Track;
import io.hypersistence.utils.hibernate.type.json.JsonType;import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import org.hibernate.Hibernate;import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Runner implements AppUser {
  @Id private String id;

  private String name;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<AppTrack> favoriteTracks = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<AppTrack> rejectedTracks = new ArrayList<>();

  @JsonBackReference
  @OneToMany(mappedBy = "runner", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Playlist> playlists = new ArrayList<>();

  @Override
  public void addPlaylist(AppPlaylist appPlaylist) {
    Playlist playlist = (Playlist) appPlaylist;
    playlists.add(playlist);
    playlist.setRunner(this);
  }

  @Override
  public void removePlaylist(AppPlaylist appPlaylist) {
    Playlist playlist = (Playlist) appPlaylist;
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

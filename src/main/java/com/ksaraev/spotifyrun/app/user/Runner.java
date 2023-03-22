package com.ksaraev.spotifyrun.app.user;

import com.ksaraev.spotifyrun.app.playlist.Playlist;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Runner implements AppUser {

  @Id @GeneratedValue @UuidGenerator private UUID uuid;

  private String spotifyId;

  @OneToMany(mappedBy = "runner", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Playlist> playlists = new ArrayList<>();

  @Override
  public String getExternalId() {
    return this.spotifyId;
  }

  @Override
  public void setExternalId(String id) {
    this.spotifyId = id;
  }

  public void addPlaylist(Playlist playlist) {
    playlists.add(playlist);
    playlist.setRunner(this);
  }

  public void removePlaylist(Playlist playlist) {
    playlists.remove(playlist);
    playlist.setRunner(null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Runner runner = (Runner) o;
    return getUuid() != null && Objects.equals(getUuid(), runner.getUuid());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

package com.ksaraev.spotifyrunning.model.artist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist implements SpotifyArtist {
  private String id;
  private String name;
  private URI uri;
  private List<String> genres;

  @Override
  public String toString() {
    return "Artist(id:%s, name:%s)".formatted(this.id, this.name);
  }
}

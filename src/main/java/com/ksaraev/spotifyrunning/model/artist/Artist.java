package com.ksaraev.spotifyrunning.model.artist;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
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
}

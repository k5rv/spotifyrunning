package com.ksaraev.spotifyrun.model.artist;

import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class Artist implements SpotifyArtist {
  private String id;
  private String name;
  private URI uri;
  private List<String> genres;
}

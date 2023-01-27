package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class Track implements SpotifyTrack {
  private String id;
  private String name;
  private URI uri;
  private Integer popularity;
  private List<SpotifyArtist> artists;
}

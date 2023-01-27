package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class Track implements SpotifyTrack {
  @NotNull private String id;
  private String name;
  private URI uri;
  private Integer popularity;
  private List<SpotifyArtist> artists;
}

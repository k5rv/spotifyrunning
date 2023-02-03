package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
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

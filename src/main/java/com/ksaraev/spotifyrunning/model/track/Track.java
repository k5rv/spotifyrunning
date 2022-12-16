package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track implements SpotifyTrack {
  private String id;
  private String name;
  private URI uri;
  private Integer popularity;
  private List<SpotifyArtist> artists;
}

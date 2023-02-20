package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Track implements SpotifyTrack {
  @NotNull private String id;

  @NotEmpty private String name;

  @NotNull private URI uri;

  @Min(0)
  @Max(100)
  private Integer popularity;

  @NotEmpty private List<SpotifyArtist> artists;
}

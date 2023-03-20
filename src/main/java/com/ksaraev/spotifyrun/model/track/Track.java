package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Track implements SpotifyTrack {
  @NotNull private String id;

  @NotEmpty private String name;

  @NotNull private URI uri;

  @Min(0)
  @Max(100)
  private Integer popularity;

  @Valid @NotEmpty private List<SpotifyArtist> artists;
}

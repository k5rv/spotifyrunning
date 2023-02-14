package com.ksaraev.spotifyrun.model.user;

import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements SpotifyUser {
  @NotNull private String id;
  private String name;
  private URI uri;
  private String email;
}

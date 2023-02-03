package com.ksaraev.spotifyrun.model.user;

import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.net.URI;

@Data
public class User implements SpotifyUser {
  @NotNull private String id;
  private String name;
  private URI uri;
  private String email;
}

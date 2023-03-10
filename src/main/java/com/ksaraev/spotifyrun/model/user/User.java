package com.ksaraev.spotifyrun.model.user;

import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User implements SpotifyUser {
  @NotNull private String id;
  @NotEmpty private String name;
  @Email private String email;
  @NotNull private URI uri;
}

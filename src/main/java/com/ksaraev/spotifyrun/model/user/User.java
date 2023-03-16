package com.ksaraev.spotifyrun.model.user;

import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "spotify_user")
public class User implements SpotifyUser {
  @Id @NotNull private String id;
  @NotEmpty private String name;
  @Email private String email;
  @NotNull private URI uri;
}

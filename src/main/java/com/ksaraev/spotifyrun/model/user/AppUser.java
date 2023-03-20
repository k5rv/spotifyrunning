package com.ksaraev.spotifyrun.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
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
public class AppUser implements SpotifyUser {
  @NotNull @Id private String id;
  @NotEmpty private String name;

  @Email private String email;
  @NotNull private URI uri;
}

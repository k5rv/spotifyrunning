package com.ksaraev.spotifyrunning.model.user;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements SpotifyUser {
  private String id;
  private String name;
  private URI uri;
  private String email;
}

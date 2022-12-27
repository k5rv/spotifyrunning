package com.ksaraev.spotifyrunning.model.user;

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

  @Override
  public String toString() {
    return "User(id:%s, name:%s)".formatted(this.id, this.name);
  }
}

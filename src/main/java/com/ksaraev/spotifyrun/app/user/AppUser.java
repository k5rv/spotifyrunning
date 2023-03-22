package com.ksaraev.spotifyrun.app.user;

import java.util.UUID;

public interface AppUser {

  UUID getUuid();

  void setUuid(UUID uuid);

  String getExternalId();

  void setExternalId(String id);
}

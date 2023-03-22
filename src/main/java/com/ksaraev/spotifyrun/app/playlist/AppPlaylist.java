package com.ksaraev.spotifyrun.app.playlist;

import java.util.List;import java.util.UUID;

public interface AppPlaylist {

  UUID getUuid();

  void setUuid(UUID uuid);

  String getExternalId();

  void setExternalId(String id);

  List<String> getTrackIds();

  void setTrackIds(List<String> trackIds);
}

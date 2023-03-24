package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.user.AppUser;
import java.util.List;

public interface AppPlaylist {

  String getId();

  void setId(String id);

  AppUser getOwner();

  void setOwner(AppUser appUser);

  List<AppTrack> getTracks();

  void setTracks(List<AppTrack> tracks);
}

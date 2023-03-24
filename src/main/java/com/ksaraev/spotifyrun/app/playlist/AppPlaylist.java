package com.ksaraev.spotifyrun.app.playlist;


import com.ksaraev.spotifyrun.app.user.AppUser;
import java.util.List;

public interface AppPlaylist {

  String getId();

  void setId(String id);

  AppUser getOwner();

  void setOwner(AppUser appUser);

  List<String> getTrackIds();

  void setTrackIds(List<String> trackIds);
}

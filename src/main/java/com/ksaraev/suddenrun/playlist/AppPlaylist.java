package com.ksaraev.suddenrun.playlist;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.user.AppUser;

import java.util.List;

public interface AppPlaylist {

  String getId();

  void setId(String id);

  AppUser getUser();

  void setUser(AppUser appUser);

  List<AppTrack> getTracks();

  void setTracks(List<AppTrack> tracks);

  List<AppTrack> getPreferences();

  void setPreferences(List<AppTrack> preferences);

  List<AppTrack> getExclusions();

  void setExclusions(List<AppTrack> exclusions);

  String getSnapshotId();

  void setSnapshotId(String snapshotId);
}

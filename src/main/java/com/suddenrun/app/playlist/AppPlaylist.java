package com.suddenrun.app.playlist;

import com.suddenrun.app.track.AppTrack;
import com.suddenrun.app.user.AppUser;
import java.util.List;

public interface AppPlaylist {

  String getId();

  void setId(String id);

  AppUser getOwner();

  void setOwner(AppUser appUser);

  List<AppTrack> getTracks();

  void setTracks(List<AppTrack> tracks);

  List<AppTrack> getCustomTracks();

  void setCustomTracks(List<AppTrack> customTracks);

  List<AppTrack> getRejectedTracks();

  void setRejectedTracks(List<AppTrack> rejectedTracks);

  String getSnapshotId();

  void setSnapshotId(String snapshotId);
}

package com.ksaraev.utils.helpers;

import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SuddenrunHelper {

  public static SuddenrunUser getUser() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    return SuddenrunUser.builder().id(id).name(name).build();
  }

  public static SuddenrunTrack getTrack() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    return SuddenrunTrack.builder().id(id).name(name).build();
  }

  public static List<AppTrack> getTracks(Integer size) {
    List<AppTrack> appTracks = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> appTracks.add(index, getTrack()));
    return appTracks;
  }

  public static SuddenrunPlaylist getSuddenrunPlaylist() {
    String id = SpotifyResourceHelper.getRandomId();
    SuddenrunUser user = getUser();
    List<SuddenrunTrack> customTracks =
        getTracks(3).stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
    List<SuddenrunTrack> rejectedTracks =
        getTracks(2).stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
    List<SuddenrunTrack> tracks =
        getTracks(10).stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SuddenrunPlaylist.builder()
        .id(id)
        .owner(user)
        .customTracks(customTracks)
        .rejectedTracks(rejectedTracks)
        .tracks(tracks)
        .snapshotId(snapshotId)
        .build();
  }

  public static SuddenrunPlaylist getSuddenrunPlaylist(SuddenrunUser user) {
    String id = SpotifyResourceHelper.getRandomId();
    List<SuddenrunTrack> customTracks =
            getTracks(3).stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
    List<SuddenrunTrack> rejectedTracks =
            getTracks(2).stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
    List<SuddenrunTrack> tracks =
            getTracks(10).stream().map(SuddenrunTrack.class::cast).collect(Collectors.toList());
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SuddenrunPlaylist.builder()
            .id(id)
            .owner(user)
            .customTracks(customTracks)
            .rejectedTracks(rejectedTracks)
            .tracks(tracks)
            .snapshotId(snapshotId)
            .build();
  }
}

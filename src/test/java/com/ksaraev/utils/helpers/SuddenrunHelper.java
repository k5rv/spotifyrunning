package com.ksaraev.utils.helpers;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.SuddenrunUser;

import java.util.ArrayList;
import java.util.List;
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
}

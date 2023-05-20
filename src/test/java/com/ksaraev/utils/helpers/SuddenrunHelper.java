package com.ksaraev.utils.helpers;

import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.SuddenrunUser;

public class SuddenrunHelper {

  public static AppUser getUser() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    return SuddenrunUser.builder().id(id).name(name).build();
  }
}

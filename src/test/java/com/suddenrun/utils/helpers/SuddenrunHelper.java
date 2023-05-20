package com.suddenrun.utils.helpers;

import static com.suddenrun.utils.helpers.SpotifyResourceHelper.*;

import com.suddenrun.app.user.AppUser;
import com.suddenrun.app.user.SuddenrunUser;

public class SuddenrunHelper {

  public static AppUser getUser() {
    String id = getRandomId();
    String name = getRandomName();
    return SuddenrunUser.builder().id(id).name(name).build();
  }
}

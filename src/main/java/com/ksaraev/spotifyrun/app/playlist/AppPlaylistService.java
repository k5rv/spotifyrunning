package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.user.AppUser;

public interface AppPlaylistService {

  AppPlaylist getPlaylist(AppUser appUser);

  AppPlaylist createPlaylist(AppUser appUser);

  void addMusic(AppPlaylist appPlaylist);

  void updateMusic(AppPlaylist appPlaylist);
}

package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.user.AppUser;

public interface AppPlaylistService {

  boolean isRelationExists(AppUser appUser);

  AppPlaylist getPlaylist(AppUser appUser);

  AppPlaylist createPlaylist(AppUser appUser);

  void addMusic(AppPlaylist appPlaylist);
}

package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.user.AppUser;

public interface AppPlaylistService {

  boolean playlistExists(AppUser appUser);

  AppPlaylist getPlaylist(AppUser appUser);

  AppPlaylist createPlaylist(AppUser appUser);

  void addMusicRecommendations(AppPlaylist appPlaylist);
}

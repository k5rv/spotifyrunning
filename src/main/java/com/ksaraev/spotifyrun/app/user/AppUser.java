package com.ksaraev.spotifyrun.app.user;


import com.ksaraev.spotifyrun.app.playlist.AppPlaylist;
import java.util.List;

public interface AppUser {

  String getId();

  void setId(String id);

  String getName();

  void setName(String name);

  List<AppPlaylist> getPlaylists();

  void setPlaylists(List<AppPlaylist> playlists);
}

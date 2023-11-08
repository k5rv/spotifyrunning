package com.ksaraev.suddenrun.analysis;

import com.ksaraev.suddenrun.playlist.AppPlaylist;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;

public interface AppAudioAudioAnalysisService {
  void consume(SuddenrunPlaylist playlist);

  void send(AppPlaylist playlist);
}

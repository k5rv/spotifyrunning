package com.ksaraev.suddenrun.analysis;

import com.ksaraev.suddenrun.playlist.AppPlaylist;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunAudioAnalysisService implements AppAudioAudioAnalysisService {

  private final SuddenrunPlaylistProducer producer;

  // @Async
  @Override
  @KafkaListener(topics = "suddenrun-playlists", groupId = "suddenrun")
  public void consume(SuddenrunPlaylist playlist) {}

  @Override
  public void send(AppPlaylist playlist) {
    producer.sendMessage("suddenrun-playlists", (SuddenrunPlaylist) playlist);
  }
}

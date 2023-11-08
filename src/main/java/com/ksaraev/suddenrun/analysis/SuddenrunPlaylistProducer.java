package com.ksaraev.suddenrun.analysis;

import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SuddenrunPlaylistProducer {
  private final KafkaTemplate<String, SuddenrunPlaylist> template;

  public void sendMessage(String topic, SuddenrunPlaylist playlist) {
    template.send(topic, playlist);
  }
}
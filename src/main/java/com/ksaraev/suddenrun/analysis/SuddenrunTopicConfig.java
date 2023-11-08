package com.ksaraev.suddenrun.analysis;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SuddenrunTopicConfig {
  @Bean
  public NewTopic suddenrunPlaylistsTopic() {
    return TopicBuilder.name("suddenrun-playlists").build();
  }
}

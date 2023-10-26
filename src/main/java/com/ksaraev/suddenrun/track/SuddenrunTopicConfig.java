package com.ksaraev.suddenrun.track;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SuddenrunTopicConfig {
  @Bean
  public NewTopic spotifyTopic() {
    return TopicBuilder.name("spotify-recommendations-seeds").build();
  }
}

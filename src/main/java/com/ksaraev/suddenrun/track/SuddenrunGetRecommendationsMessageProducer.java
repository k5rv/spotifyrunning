package com.ksaraev.suddenrun.track;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SuddenrunGetRecommendationsMessageProducer {
  private final KafkaTemplate<String, SuddenrunGetRecommendationsMessage> template;

  public void sendMessage(String topic, SuddenrunGetRecommendationsMessage message) {
    template.send(topic, message);
  }
}
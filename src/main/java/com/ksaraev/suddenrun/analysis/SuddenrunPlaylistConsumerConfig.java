package com.ksaraev.suddenrun.analysis;

import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class SuddenrunPlaylistConsumerConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  public Map<String, Object> consumerConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    return config;
  }

  @Bean
  public ConsumerFactory<String, SuddenrunPlaylist> consumerFactory() {
    JsonDeserializer<SuddenrunPlaylist> jsonDeserializer = new JsonDeserializer<>();
    jsonDeserializer.addTrustedPackages("com.ksaraev.*");
    return new DefaultKafkaConsumerFactory<>(
        consumerConfig(), new StringDeserializer(), jsonDeserializer);
  }

  @Bean
  public KafkaListenerContainerFactory<
          ConcurrentMessageListenerContainer<String, SuddenrunPlaylist>>
      kafkaListenerContainerFactory(ConsumerFactory<String, SuddenrunPlaylist> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, SuddenrunPlaylist> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }
}

package com.ksaraev.spotifyrun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpotifyRunApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpotifyRunApplication.class, args);
  }
}

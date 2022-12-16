package com.ksaraev.spotifyrunning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpotifyRunningApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpotifyRunningApplication.class, args);
  }
}

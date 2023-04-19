package com.ksaraev.spotifyrun.security;

import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile(value = {"development", "production", "docker", "kube"})
public class AppSecurityConfig {

  @Bean
  public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
    return http.csrf()
        .disable()
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(GET, "/actuator/health").permitAll())
        .authorizeHttpRequests(authorize -> authorize.requestMatchers(GET, "/*").authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(POST, "/api/v1/playlists").authenticated())
        .oauth2Login(Customizer.withDefaults())
        .build();
  }
}

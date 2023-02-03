package com.ksaraev.spotifyrun.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

  @Bean
  public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
    return http.csrf()
        .disable()
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .oauth2Login(Customizer.withDefaults())
        .build();
  }
}

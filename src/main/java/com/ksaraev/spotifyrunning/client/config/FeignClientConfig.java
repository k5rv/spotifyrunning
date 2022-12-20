package com.ksaraev.spotifyrunning.client.config;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
@AllArgsConstructor
public class FeignClientConfig {
  private final OAuth2AuthorizedClientService authorizedClientService;

  @Bean
  public RequestInterceptor spotifyClientRequestInterceptor() {
    return requestTemplate ->
        requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());
  }

  public String getAccessToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    OAuth2AuthorizedClient client =
        this.authorizedClientService.loadAuthorizedClient("spotify", authentication.getName());
    return client.getAccessToken().getTokenValue();
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.BASIC;
  }
}

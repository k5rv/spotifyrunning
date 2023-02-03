package com.ksaraev.spotifyrun.client.config;

import com.ksaraev.spotifyrun.client.config.encoders.SpotifyClientRequestQueryMapEncoder;
import com.ksaraev.spotifyrun.client.exception.FeignExceptionHandler;
import com.ksaraev.spotifyrun.client.exception.SpotifyExceptionHandler;
import feign.Logger;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Getter
@Configuration
@AllArgsConstructor
public class SpotifyClientFeignConfig {

  private final OAuth2AuthorizedClientService authorizedClientService;

  @Bean
  public RequestInterceptor requestInterceptor() {
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
  public QueryMapEncoder spotifyClientRequestQueryMapEncoder() {
    return new SpotifyClientRequestQueryMapEncoder();
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public FeignExceptionHandler spotifyExceptionHandler() {
    return new SpotifyExceptionHandler();
  }
}

package com.ksaraev.spotifyrun.client.config.formatters;

import com.ksaraev.spotifyrun.client.config.converters.SpotifyClientRequestParameterConverter;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;

@Configuration
public class SpotifyClientFeignFormatterRegistrar implements FeignFormatterRegistrar {
  @Override
  public void registerFormatters(FormatterRegistry registry) {
    registry.addConverter(new SpotifyClientRequestParameterConverter());
  }
}

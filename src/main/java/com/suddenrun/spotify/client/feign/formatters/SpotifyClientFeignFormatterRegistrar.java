package com.suddenrun.spotify.client.feign.formatters;

import com.suddenrun.spotify.client.feign.converters.SpotifyClientRequestParameterConverter;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

@Component
public class SpotifyClientFeignFormatterRegistrar implements FeignFormatterRegistrar {
  @Override
  public void registerFormatters(FormatterRegistry registry) {
    registry.addConverter(new SpotifyClientRequestParameterConverter());
  }
}

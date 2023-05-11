package com.suddenrun.client.feign.formatters;

import com.suddenrun.client.feign.converters.SpotifyClientRequestParameterConverter;
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

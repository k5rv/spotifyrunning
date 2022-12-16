package com.ksaraev.spotifyrunning.client.config.converters;

import lombok.NoArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SpotifyClientRequestParameterConverter
    implements Converter<SpotifyClientRequestParameter, String> {
  @Override
  public String convert(SpotifyClientRequestParameter source) {
    return source.getParameter();
  }
}

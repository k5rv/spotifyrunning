package com.ksaraev.spotifyrun.client.feign.config.converters;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpotifyClientRequestParameterConverterTest {

  private SpotifyClientRequestParameterConverter underTest;

  @BeforeEach
  void setUp() {
    underTest = new SpotifyClientRequestParameterConverter();
  }

  @Test
  void itShouldConvert() {
    // Given
    String parameter = "parameter";
    SpotifyClientRequestParameter spotifyClientRequestParameter = () -> parameter;
    // When and Then
    Assertions.assertThat(underTest.convert(spotifyClientRequestParameter)).isEqualTo(parameter);
  }
}

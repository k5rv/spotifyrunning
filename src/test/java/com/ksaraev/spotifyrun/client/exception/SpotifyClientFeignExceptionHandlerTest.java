package com.ksaraev.spotifyrun.client.exception;

import com.ksaraev.spotifyrun.client.exception.http.SpotifyException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

class SpotifyClientFeignExceptionHandlerTest {

  private SpotifyClientFeignExceptionHandler underTest;

  @BeforeEach
  void setUp() {
    underTest = new SpotifyClientFeignExceptionHandler();
  }

  @ParameterizedTest
  @CsvSource({
    "304, SpotifyNotModifiedException",
    "400, SpotifyBadRequestException",
    "401, SpotifyUnauthorizedException",
    "403, SpotifyForbiddenException",
    "404, SpotifyNotFoundException",
    "429, SpotifyTooManyRequestsException",
    "500, SpotifyInternalServerErrorException",
    "502, SpotifyBadGatewayException",
    "503, SpotifyServiceUnavailableException"
  })
  void itShouldReturnSpotifyExceptionWhenHttpResponseErrorStatusReceived(
      Integer status, String className) throws Exception {
    // Given
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(status)
            .build();
    // When and Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(
            Class.forName("com.ksaraev.spotifyrun.client.exception.http." + className));
  }

  @Test
  void itShouldReturnSpotifyExceptionWhenUnmappedHttpResponseErrorStatusReceived() {
    // Given
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(422)
            .build();
    // When and Then
    Assertions.assertThat(underTest.handle(response)).isExactlyInstanceOf(SpotifyException.class);
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseBodyCantBeRead() {
    // Given
    Response response =
        Response.builder()
            .body(
                new InputStream() {
                  @Override
                  public int read() throws IOException {
                    throw new IOException("Error occurred");
                  }
                },
                1)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(400)
            .build();

    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.handle(response))
        .isExactlyInstanceOf(SpotifyClientErrorResponseHandlingException.class)
        .hasMessage("Error while reading Spotify API error response body");
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseIsNull() {
    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.handle(null))
        .isExactlyInstanceOf(SpotifyClientErrorResponseHandlingException.class)
        .hasMessage("Error while reading Spotify API error response: response is null");
  }
}

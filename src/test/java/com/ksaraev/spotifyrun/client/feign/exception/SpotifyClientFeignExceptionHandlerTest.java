package com.ksaraev.spotifyrun.client.feign.exception;

import com.ksaraev.spotifyrun.client.exception.SpotifyClientReadingErrorResponseException;
import com.ksaraev.spotifyrun.client.exception.SpotifyClientReadingErrorResponseIsNullException;
import com.ksaraev.spotifyrun.client.feign.exception.http.SpotifyBadRequestException;
import com.ksaraev.spotifyrun.client.feign.exception.http.SpotifyException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyClientFeignExceptionHandlerTest {

  private SpotifyClientFeignExceptionHandler underTest;

  @BeforeEach
  void setUp() {
    underTest = new SpotifyClientFeignExceptionHandler();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           304|SpotifyNotModifiedException
           400|SpotifyBadRequestException
           401|SpotifyUnauthorizedException
           403|SpotifyForbiddenException
           404|SpotifyNotFoundException
           429|SpotifyTooManyRequestsException
           500|SpotifyInternalServerErrorException
           502|SpotifyBadGatewayException
           503|SpotifyServiceUnavailableException
           """)
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
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(
            Class.forName(SpotifyException.class.getPackage().getName() + "." + className));
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
    // Then
    Assertions.assertThat(underTest.handle(response)).isExactlyInstanceOf(SpotifyException.class);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           400|SpotifyBadRequestException  |{"error":{"status":400,"message":"Bad Request"}}
           401|SpotifyUnauthorizedException|{"error":"invalid_client","error_description":"Invalid client secret"}
           401|SpotifyUnauthorizedException|""
           """)
  void itShouldReturnSpotifyExceptionWithOriginalErrorMessageWhenHttpResponseErrorStatusReceived(
      Integer status, String className, String message) throws Exception {
    // Given
    Response response =
        Response.builder()
            .body(message, StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(status)
            .build();
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(
            Class.forName(SpotifyException.class.getPackage().getName() + "." + className))
        .hasMessage(message);
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseBodyCantBeRead() {
    // Given
    String message = "message";
    Response response =
        Response.builder()
            .body(
                new InputStream() {
                  @Override
                  public int read() throws IOException {
                    throw new IOException(message);
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
    // Then
    Assertions.assertThatThrownBy(() -> underTest.handle(response))
        .isExactlyInstanceOf(SpotifyClientReadingErrorResponseException.class)
        .hasMessage(
            SpotifyClientReadingErrorResponseException.UNABLE_TO_READ_ERROR_RESPONSE + message);
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseBodyIsNull() {
    // Given
    String message = "";
    byte[] bytes = null;
    Response response =
        Response.builder()
            .body(bytes)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(400)
            .build();
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(SpotifyBadRequestException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldThrowSpotifyClientErrorResponseHandlingExceptionWhenResponseIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.handle(null))
        .isExactlyInstanceOf(SpotifyClientReadingErrorResponseIsNullException.class)
        .hasMessage(READING_ERROR_RESPONSE_IS_NULL);
  }
}

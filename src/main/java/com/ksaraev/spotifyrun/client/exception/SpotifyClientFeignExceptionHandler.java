package com.ksaraev.spotifyrun.client.exception;

import com.ksaraev.spotifyrun.client.exception.http.*;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.ksaraev.spotifyrun.client.exception.SpotifyClientErrorResponseHandlingException.ERROR_WHILE_READING_SPOTIFY_API_ERROR_RESPONSE_BODY;
import static com.ksaraev.spotifyrun.client.exception.SpotifyClientErrorResponseHandlingException.RESPONSE_IS_NULL;

@Slf4j
@Component
public class SpotifyClientFeignExceptionHandler implements FeignExceptionHandler {

  @Override
  public Exception handle(Response response) {
    if (response == null) {
      throw new SpotifyClientErrorResponseHandlingException(
          ERROR_WHILE_READING_SPOTIFY_API_ERROR_RESPONSE_BODY + RESPONSE_IS_NULL);
    }
    try {
      String message =
          response.body() != null
              ? new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8)
              : "";
      HttpStatus status = HttpStatus.valueOf(response.status());
      switch (status) {
        case NOT_MODIFIED -> {
          return new SpotifyNotModifiedException(message);
        }
        case BAD_REQUEST -> {
          return new SpotifyBadRequestException(message);
        }
        case UNAUTHORIZED -> {
          return new SpotifyUnauthorizedException(message);
        }
        case FORBIDDEN -> {
          return new SpotifyForbiddenException(message);
        }
        case NOT_FOUND -> {
          return new SpotifyNotFoundException(message);
        }
        case TOO_MANY_REQUESTS -> {
          return new SpotifyTooManyRequestsException(message);
        }
        case INTERNAL_SERVER_ERROR -> {
          return new SpotifyInternalServerErrorException(message);
        }
        case BAD_GATEWAY -> {
          return new SpotifyBadGatewayException(message);
        }
        case SERVICE_UNAVAILABLE -> {
          return new SpotifyServiceUnavailableException(message);
        }
        default -> {
          return new SpotifyException(message);
        }
      }
    } catch (IOException | RuntimeException e) {
      throw new SpotifyClientErrorResponseHandlingException(
          ERROR_WHILE_READING_SPOTIFY_API_ERROR_RESPONSE_BODY, e);
    }
  }
}

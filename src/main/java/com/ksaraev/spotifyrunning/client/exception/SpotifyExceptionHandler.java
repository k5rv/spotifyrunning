package com.ksaraev.spotifyrunning.client.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksaraev.spotifyrunning.client.exception.http.*;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class SpotifyExceptionHandler implements FeignExceptionHandler {

  ObjectMapper objectMapper =
      new ObjectMapper().configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

  @Override
  public Exception handle(Response response) {
    SpotifyApiError error;

    try {
      error = objectMapper.readValue(response.body().asInputStream(), SpotifyApiError.class);
      log.error(
          "Received Spotify API error response: status={}, message={}, path={}",
          error.getStatus(),
          error.getMessage(),
          response.request().url());
    } catch (IOException e) {
      throw new SpotifyClientException("Error while deserializing response body");
    }

    HttpStatus status = HttpStatus.resolve(response.status());
    if (Objects.isNull(status))
      throw new SpotifyClientException("Error while resolving response http status");
    switch (status) {
      case NOT_MODIFIED -> throw new SpotifyNotModifiedException(error.getMessage());
      case BAD_REQUEST -> throw new SpotifyBadRequestException(error.getMessage());
      case UNAUTHORIZED -> throw new SpotifyUnauthorizedException(error.getMessage());
      case FORBIDDEN -> throw new SpotifyForbiddenException(error.getMessage());
      case NOT_FOUND -> throw new SpotifyNotFoundException(error.getMessage());
      case TOO_MANY_REQUESTS -> throw new SpotifyTooManyRequestsException(error.getMessage());
      case INTERNAL_SERVER_ERROR -> throw new SpotifyInternalServerError(error.getMessage());
      case BAD_GATEWAY -> throw new SpotifyBadGatewayException(error.getMessage());
      case SERVICE_UNAVAILABLE -> throw new SpotifyServiceUnavailableException(error.getMessage());
      default -> throw new SpotifyException(error.getMessage());
    }
  }
}

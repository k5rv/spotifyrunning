package com.ksaraev.spotifyrun.client.exception;

import com.ksaraev.spotifyrun.client.exception.http.*;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class SpotifyExceptionHandler implements ClientExceptionHandler {

  @Override
  public Exception handle(Response response) {
    try {
      String message =
          new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
      HttpStatus status = HttpStatus.resolve(response.status());
      if (status == null)
        throw new SpotifyClientReadingHttpResponseException(
            "Error while resolving Spotify API error response. Http status is null.");
      switch (status) {
        case NOT_MODIFIED -> throw new SpotifyNotModifiedException(message);
        case BAD_REQUEST -> throw new SpotifyBadRequestException(message);
        case UNAUTHORIZED -> throw new SpotifyUnauthorizedException(message);
        case FORBIDDEN -> throw new SpotifyForbiddenException(message);
        case NOT_FOUND -> throw new SpotifyNotFoundException(message);
        case TOO_MANY_REQUESTS -> throw new SpotifyTooManyRequestsException(message);
        case INTERNAL_SERVER_ERROR -> throw new SpotifyInternalServerErrorException(message);
        case BAD_GATEWAY -> throw new SpotifyBadGatewayException(message);
        case SERVICE_UNAVAILABLE -> throw new SpotifyServiceUnavailableException(message);
        default -> throw new SpotifyException(message);
      }
    } catch (IOException e) {
      throw new SpotifyClientReadingHttpResponseException(
          "Error while reading Spotify API error response body.", e);
    }
  }
}

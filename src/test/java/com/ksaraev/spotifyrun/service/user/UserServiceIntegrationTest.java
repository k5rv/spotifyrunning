package com.ksaraev.spotifyrun.service.user;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.GetUserException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.exception.GetUserException.UNABLE_TO_GET_USER;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED;
import static com.ksaraev.spotifyrun.exception.UserNotFoundException.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserServiceIntegrationTest {

  public static WireMockServer mock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired private UserService underTest;

  @BeforeAll
  static void setupClass() {
    mock.start();
  }

  @AfterAll
  static void clean() {
    mock.shutdown();
  }

  @AfterEach
  void after() {
    mock.resetAll();
  }

  @Test
  void itShouldGetCurrentUserProfile() {
    // Given
    String id = "12122604372";
    String name = "Konstantin";
    String email = "mail@gmail.com";
    URI uri = URI.create("spotify:user:12122604372");
    String responseBody =
        "{\n"
            + "  \"display_name\":\""
            + name
            + "\",\n"
            + "  \"external_urls\":{\n"
            + "    \"spotify\":\"https://open.spotify.com/user/12122604372\"\n"
            + "  },\n"
            + "  \"followers\":{\n"
            + "    \"href\":null,\n"
            + "    \"total\":0\n"
            + "  },\n"
            + "  \"href\":\"https://api.spotify.com/v1/users/12122604372\",\n"
            + "  \"id\":\""
            + id
            + "\",\n"
            + "  \"email\":\""
            + email
            + "\",\n"
            + "  \"images\":[\n"
            + "    {\n"
            + "      \"height\":null,\n"
            + "      \"url\":\"https://scontent-ams2-1.xx.fbcdn.net\",\n"
            + "      \"width\":null\n"
            + "    }\n"
            + "  ],\n"
            + "  \"type\":\"user\",\n"
            + "  \"uri\":\""
            + uri
            + "\"\n"
            + "}";
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // When and Then
    assertThat(underTest.getUser()).isNotNull().isEqualTo(new User(id, name, uri, email));
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenUserProfileIdIsNotPresent() {
    // Given
    String responseBody =
        """
               {
                 "display_name":"Konstantin",
                 "external_urls":{
                   "spotify":"https://open.spotify.com/user/12122604372"
                 },
                 "followers":{
                   "href":null,
                   "total":0
                 },
                 "href":"https://api.spotify.com/v1/users/12122604372",
                 "email":"mail@gmail.com",
                 "images":[
                   {
                     "height":null,
                     "url":"https://scontent-ams2-1.xx.fbcdn.net",
                     "width":null
                   }
                 ],
                 "type":"user",
                 "uri":"spotify:user:12122604372"
               }
               """;

    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage("getUser.<return value>.id: must not be null");
  }

  @Test
  void itShouldThrowUserNotFoundExceptionWhenHttpStatusCode404() {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withStatus(404)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UserNotFoundException.class)
        .hasMessage(USER_NOT_FOUND + ": ");
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
                 {"error":{"status":400,"message":"Bad Request"}},
                 {"error":"invalid_client","error_description":"Invalid client secret"}
                 Plain text
                 """,
      delimiter = '|')
  void itShouldThrowUnauthorizedExceptionWhenHttpStatusCode401(String responseBody) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)
                    .withStatus(401)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + ": " + responseBody);
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
                 400|{"error":{"status":400,"message":"Bad Request"}},
                 403|{"error":{"status":403,"message":"Forbidden"}},
                 429|{"error":{"status":429,"message":"Too Many Requests"}},
                 500|{"error":{"status":500,"message":"Internal Server Error"}},
                 502|{"error":{"status":502,"message":"Bad Gateway"}},
                 503|{"error":{"status":503,"message":"Service Unavailable"}},
                 400|{"error":"invalid_client","error_description":"Invalid client secret"}
                 400|Plain text
                 """,
      delimiter = '|')
  void itShouldThrowGetUserExceptionWhenHttpStatusCodeNot2XX(Integer status, String responseBody) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)
                    .withStatus(status)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + ": " + responseBody);
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
                 {"id:"100",name":"something","size":"20"}
                 Plain text
                 """,
      delimiter = '|')
  void
      itShouldThrowGetUserExceptionWhenHttpStatusCodeIs200AndResponseBodyIsNotSpotifyUserProfileItemJson(
          String responseBody) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(
            UNABLE_TO_GET_USER
                + ": "
                + "Error while extracting response for type [class "
                + SpotifyUserProfileItem.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

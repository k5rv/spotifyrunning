package com.ksaraev.spotifyrun.service.user;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.GetUserException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.ksaraev.spotifyrun.exception.GetUserException.GET_USER_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.exception.UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserServiceIntegrationTest {

  @Autowired private UserService underTest;

  private WireMockServer mock;

  @BeforeAll
  void setUp() {
    mock = new WireMockServer();
    mock.start();
  }

  @AfterEach
  void reset() {
    mock.resetAll();
  }

  @AfterAll
  void shutDown() {
    mock.shutdown();
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
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
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
            }""";

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
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
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withStatus(404)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UserNotFoundException.class)
        .hasMessage(USER_NOT_FOUND_EXCEPTION_MESSAGE + ": ");
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
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)
                    .withStatus(401)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED_EXCEPTION_MESSAGE + ": " + responseBody);
  }

  @ParameterizedTest
  @CsvSource({"400", "403", "429", "500", "502", "503"})
  void itShouldThrowGetUserExceptionWhenHttpStatusCodeNot2XX(Integer status) {
    // Given
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withStatus(status)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(GET_USER_EXCEPTION_MESSAGE + ": ");
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
          400|{"error":{"status":400,"message":"Bad Request"}},
          503|{"error":{"status":503,"message":"Service Unavailable"}},
          400|{"error":"invalid_client","error_description":"Invalid client secret"}
          422|Plain text
          """,
      delimiter = '|')
  void itShouldThrowGetUserExceptionWithSpotifyErrorMessageWhenHttpStatusCodeNot2XX(
      Integer status, String responseBody) {
    // Given
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)
                    .withStatus(status)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(GET_USER_EXCEPTION_MESSAGE + ": " + responseBody);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenResponseIsNotJson() {
    // Given
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody("Not a JSON string")));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(
            GET_USER_EXCEPTION_MESSAGE
                + ": "
                + "Error while extracting response for type [class "
                + SpotifyUserProfileItem.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

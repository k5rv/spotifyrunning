package com.ksaraev.spotifyrun.service.user;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.GetUserException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.UserService;
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
  void itShouldReturnUser() {
    // Given
    String id = "12122604372";
    String name = "Konstantin";
    String email = "email@gmail.com";
    URI uri = URI.create("spotify:user:12122604372");
    User user = User.builder().id(id).name(name).email(email).uri(uri).build();
    String spotifyUserProfileItemJson =
        """
             {
               "country": "CC",
               "display_name": "%s",
               "email": "%s",
               "explicit_content": {
                 "filter_enabled": false,
                 "filter_locked": false
               },
               "external_urls": {
                 "spotify": "https://open.spotify.com/user/12122604372"
               },
               "followers": {
                 "href": null,
                 "total": 0
               },
               "href": "https://api.spotify.com/v1/users/12122604372",
               "id": "%s",
               "images": [
                 {
                   "height": null,
                   "url": "https://scontent-cdg2-1.xx.fbcdn.net",
                   "width": null
                 }
               ],
               "product": "premium",
               "type": "user",
               "uri": "%s"
             }
             """
            .formatted(name, email, id, uri);

    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(spotifyUserProfileItemJson)));
    // Then
    assertThat(underTest.getUser())
        .isNotNull()
        .isEqualTo(user)
        .hasOnlyFields("id", "name", "email", "uri");
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               "key":"value",              |"email":"k.saraev@gmail.com",|"id":"12122604372",|"uri":"spotify:user:12122604372"|.displayName: must not be empty
               "display_name":"Konstantin",|"email":"email@",            |"id":"12122604372",|"uri":"spotify:user:12122604372"|.email: must be a well-formed email address
               "display_name":"Konstantin",|"email":"k.saraev@gmail.com",|"key":"value",     |"uri":"spotify:user:12122604372"|.id: must not be null
               "display_name":"Konstantin",|"email":"k.saraev@gmail.com",|"id":"12122604372",|"key":"value"                   |.uri: must not be null
               """)
  void itShouldThrowGetUserExceptionWhenSpotifyReturnsNotValidUserProfileItem(
      String displayNameJsonKeyValue,
      String emailJsonKeyValue,
      String idJsonKeyValue,
      String uriJsonKeyValue,
      String constraintViolationMessage) {
    String spotifyUserProfileItemJson =
        """
             {
               "country":"CY",
               %s
               %s
               "explicit_content":{
                 "filter_enabled":false,
                 "filter_locked":false
               },
               "external_urls":{
                 "spotify":"https://open.spotify.com/user/12122604372"
               },
               "followers":{
                 "href":null,
                 "total":0
               },
               "href":"https://api.spotify.com/v1/users/12122604372",
               %s
               "images":[
                 {
                   "height":null,
                   "url":"https://scontent-cdt1-1.xx.fbcdn.net",
                   "width":null
                 }
               ],
               "product":"premium",
               "type":"user",
               %s
             }
             """
            .formatted(displayNameJsonKeyValue, emailJsonKeyValue, idJsonKeyValue, uriJsonKeyValue);

    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(spotifyUserProfileItemJson)));
    // Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(
            UNABLE_TO_GET_USER
                + "getCurrentUserProfile.<return value>"
                + constraintViolationMessage);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenSpotifyReturnsEmptyResponseBody() {
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)));
    // Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + "getCurrentUserProfile.<return value>: must not be null");
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               {"error":{"status":404,"message":"Not Found"}}
               plain text
               ""
               """)
  void itShouldThrowUserNotFoundExceptionWhenSpotifyResponseHttpStatusCodeIs404(String message) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(404)));
    // Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UserNotFoundException.class)
        .hasMessage(USER_NOT_FOUND + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               {"error":{"status":401,"message":"Unauthorized"}}
               {"error":"invalid_client","error_description":"Invalid client secret"}
               plain text
               ""
               """)
  void itShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs401(String message) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(401)));
    // Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                400|{"error":{"status":400,"message":"Bad Request"}},
                403|{"error":{"status":403,"message":"Forbidden"}},
                429|{"error":{"status":429,"message":"Too Many Requests"}},
                500|{"error":{"status":500,"message":"Internal Server Error"}},
                502|{"error":{"status":502,"message":"Bad Gateway"}},
                503|{"error":{"status":503,"message":"Service Unavailable"}},
                400|{"error":"invalid_client","error_description":"Invalid client secret"}
                400|plain text
                400|""
                """)
  void itShouldThrowGetUserExceptionWhenSpotifyResponseHttpStatusCodeIsNot2XX(
      Integer status, String message) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(status)));
    // Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + message);
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
               plain text
               ""
               """)
  void itShouldThrowGetUserExceptionWhenSpotifyHttpResponseBodyCantBeDecodedAsSpotifyUserProfileItem(String message) {
    // Given
    stubFor(
        get(urlEqualTo("/v1/me"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(message)));
    // Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(
            UNABLE_TO_GET_USER
                + "Error while extracting response for type [class "
                + SpotifyUserProfileItem.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

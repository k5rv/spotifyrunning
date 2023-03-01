package com.ksaraev.spotifyrun.client.user;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class GetCurrentUserProfileTest {

  private static final String GET_CURRENT_USER_PROFILE_RETURN_VALUE =
      "getCurrentUserProfile.<return value>";

  public static WireMockServer wiremock =
      new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired private SpotifyClient underTest;

  @BeforeAll
  static void setupClass() {
    wiremock.start();
  }

  @AfterAll
  static void clean() {
    wiremock.shutdown();
  }

  @AfterEach
  void after() {
    wiremock.resetAll();
  }

  @Test
  void itShouldGetCurrentUserProfile() {
    // Given
    String spotifyUserProfileItemJson =
        """
        {
          "display_name": "Konstantin",
          "external_urls": {
            "spotify": "https://open.spotify.com/user/12122604372"
          },
          "followers": {
            "href": null,
            "total": 0
          },
          "href": "https://api.spotify.com/v1/users/12122604372",
          "id": "12122604372",
          "images": [
            {
              "height": null,
              "url": "https://scontent-ams2-1.xx.fbcdn.net/1",
              "width": null
            }
          ],
          "type": "user",
          "uri": "spotify:user:12122604372"
        }
        """;

    stubFor(get(urlEqualTo("/v1/me")).willReturn(jsonResponse(spotifyUserProfileItemJson, 200)));

    // Then
    assertThat(underTest.getCurrentUserProfile())
        .isNotNull()
        .isEqualTo(jsonToObject(spotifyUserProfileItemJson, SpotifyUserProfileItem.class));
  }

  @Test
  void itShouldThrowConstraintViolationExceptionIfResponseBodyIsEmpty() {
    // Given
    String message = ": must not be null";
    stubFor(get(urlEqualTo("/v1/me")).willReturn(jsonResponse(null, 200)));
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_CURRENT_USER_PROFILE_RETURN_VALUE + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           "key":"value"              |"email":"k.saraev@gmail.com"|"id":"12122604372"|"uri":"spotify:user:12122604372"|.displayName: must not be empty
           "display_name":"Konstantin"|"email":"email@"            |"id":"12122604372"|"uri":"spotify:user:12122604372"|.email: must be a well-formed email address
           "display_name":"Konstantin"|"email":"k.saraev@gmail.com"|"key":"value"     |"uri":"spotify:user:12122604372"|.id: must not be null
           "display_name":"Konstantin"|"email":"k.saraev@gmail.com"|"id":"12122604372"|"key":"value"                   |.uri: must not be null
           """)
  void itShouldThrowConstraintViolationExceptionWhenSpotifyReturnsNotValidUserProfileItem(
      String displayNameJsonKeyValue,
      String emailJsonKeyValue,
      String idJsonKeyValue,
      String uriJsonKeyValue,
      String message) {

    String spotifyUserProfileItemJson =
        """
         {
           %s,
           %s,
           %s,
           %s,
           "country":"CY",
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
           "images":[
             {
               "height":null,
               "url":"https://scontent-cdt1-1.xx.fbcdn.net",
               "width":null
             }
           ],
           "product":"premium",
           "type":"user"
         }
         """
            .formatted(idJsonKeyValue, displayNameJsonKeyValue, uriJsonKeyValue, emailJsonKeyValue);

    stubFor(get(urlEqualTo("/v1/me")).willReturn(jsonResponse(spotifyUserProfileItemJson, 200)));
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_CURRENT_USER_PROFILE_RETURN_VALUE + message);
  }
}

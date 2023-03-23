package com.ksaraev.spotifyrun.client.dto;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class GetCurrentUserProfileIntegrationTest {

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
        .usingRecursiveComparison()
        .isEqualTo(jsonToObject(spotifyUserProfileItemJson, SpotifyUserProfileDto.class));
  }
}

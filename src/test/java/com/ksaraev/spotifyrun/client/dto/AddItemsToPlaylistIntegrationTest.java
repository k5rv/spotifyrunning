package com.ksaraev.spotifyrun.client.dto;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import java.net.URI;
import java.util.List;
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
class AddItemsToPlaylistIntegrationTest {
  public static WireMockServer wiremock =
      new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private SpotifyClient underTest;

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
  void itShouldAddItemsToPlaylist() {
    // Given
    String addItemsResponseJson =
        """
         {
           "snapshot_id":"MyxmN2I2YTZmYjQ4NTAwZTk2ZmY1ZTNjYTgzMTFlNWZkZmIwMzEzY2Y0"
         }
         """;

    String playlistId = "3zw6WSVfzWX3mj0tUuZpTK";
    URI trackUri = URI.create("spotify:track:1234567890AaBbCcDdEeFfG");
    List<URI> uris = List.of(trackUri);
    UpdatePlaylistItemsRequest updateItemsRequest =
        UpdatePlaylistItemsRequest.builder().itemUris(uris).build();

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(jsonResponse(addItemsResponseJson, 200)));

    // Then
    assertThat(underTest.addPlaylistItems(playlistId, updateItemsRequest))
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(jsonToObject(addItemsResponseJson, UpdateUpdateItemsResponse.class));
  }
}

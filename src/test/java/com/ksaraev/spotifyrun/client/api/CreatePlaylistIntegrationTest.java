package com.ksaraev.spotifyrun.client.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.utils.SpotifyHelper;
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
class CreatePlaylistIntegrationTest {

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
  void itShouldCreatePlaylist() {
    // Given
    String spotifyPlaylistItemJson =
        """
          {
            "collaborative":false,
            "description":"playlist description",
            "external_urls":{
              "spotify":"https://open.spotify.com/playlist/3zw6WSVfzWX3mj0tUuZpTK"
            },
            "followers":{
              "href":null,
              "total":0
            },
            "href":"https://api.spotify.com/v1/playlists/3zw6WSVfzWX3mj0tUuZpTK",
            "id":"3zw6WSVfzWX3mj0tUuZpTK",
            "images":[],
            "name":"playlist name",
            "owner":{
              "display_name":"Konstantin",
              "external_urls":{
                "spotify":"https://open.spotify.com/user/12122604372"
              },
              "href":"https://api.spotify.com/v1/users/12122604372",
              "id":"12122604372",
              "type":"user",
              "uri":"spotify:user:12122604372"
            },
            "primary_color":null,
            "public":false,
            "snapshot_id":"MSw4OTE2ZTk4NmVjYTU2OGE4MWE5OTE2ZWIxZGY5YmQ4NTRlMGUyYjYy",
            "tracks":{
              "href":"https://api.spotify.com/v1/playlists/3zw6WSVfzWX3mj0tUuZpTK/tracks",
              "items":[],
              "limit":100,
              "next":null,
              "offset":0,
              "previous":null,
              "total":0
            },
            "type":"playlist",
            "uri":"spotify:playlist:3zw6WSVfzWX3mj0tUuZpTK"
          }
          """;

    String userId = "12122604372";
    SpotifyPlaylistItemDetails playlistItemDetails = SpotifyHelper.getPlaylistItemDetails();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(jsonResponse(spotifyPlaylistItemJson, 200)));

    // Then
    assertThat(underTest.createPlaylist(userId, playlistItemDetails))
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class));
  }
}

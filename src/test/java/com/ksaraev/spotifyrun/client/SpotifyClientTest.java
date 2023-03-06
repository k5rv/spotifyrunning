package com.ksaraev.spotifyrun.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotifyrun.client.api.*;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.utils.JsonHelper;
import java.net.URI;
import java.util.Collections;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpotifyClientTest {

  @Autowired private SpotifyClient underTest;

  private WireMockServer wiremock;

  @BeforeAll
  void setUp() {
    wiremock = new WireMockServer();
    wiremock.start();
  }

  @AfterEach
  void reset() {
    wiremock.resetAll();
  }

  @AfterAll
  void shutDown() {
    wiremock.shutdown();
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    String userId = "12122604372";
    String spotifyPlaylistItemJson =
        """
        {
          "collaborative": false,
          "description": "New playlist description",
          "external_urls": {
            "spotify": "https://open.spotify.com/playlist/4gH6yuY8CoFjqHlbPlxVM6"
          },
          "followers": {
            "href": null,
            "total": 0
          },
          "href": "https://api.spotify.com/v1/playlists/4gH6yuY8CoFjqHlbPlxVM6",
          "id": "4gH6yuY8CoFjqHlbPlxVM6",
          "images": [],
          "name": "New Playlist",
          "owner": {
            "display_name": "Konstantin",
            "external_urls": {
              "spotify": "https://open.spotify.com/user/12122604372"
            },
            "href": "https://api.spotify.com/v1/users/12122604372",
            "id": "12122604372",
            "type": "user",
            "uri": "spotify:user:12122604372"
          },
          "primary_color": null,
          "public": false,
          "snapshot_id": "MSxlYTMyMDU1YjNlYjgyYmFlNmZiNmE3YmMwMDI0NzY5ZDZmOWU0YTIw",
          "tracks": {
            "href": "https://api.spotify.com/v1/playlists/4gH6yuY8CoFjqHlbPlxVM6/tracks",
            "items": [],
            "limit": 100,
            "next": null,
            "offset": 0,
            "previous": null,
            "total": 0
          },
          "type": "playlist",
          "uri": "spotify:playlist:4gH6yuY8CoFjqHlbPlxVM6"
        }
         """;

    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemJson)));
    // Then
    assertThat(
            underTest.createPlaylist(
                userId,
                new SpotifyPlaylistItemDetails(
                    false, false, "New Playlist", "New Playlist description")))
        .isNotNull()
        .isEqualTo(JsonHelper.jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class));
  }

  @Test
  void itShouldGetPlaylist() {
    // Given
    String playlistId = "4gH6yuY8CoFjqHlbPlxVM6";
    String spotifyPlaylistItemJson =
        """
        {
          "collaborative": false,
          "description": "New playlist description",
          "external_urls": {
            "spotify": "https://open.spotify.com/playlist/4gH6yuY8CoFjqHlbPlxVM6"
          },
          "followers": {
            "href": null,
            "total": 0
          },
          "href": "https://api.spotify.com/v1/playlists/4gH6yuY8CoFjqHlbPlxVM6",
          "id": "4gH6yuY8CoFjqHlbPlxVM6",
          "images": [],
          "name": "New Playlist",
          "owner": {
            "display_name": "Konstantin",
            "external_urls": {
              "spotify": "https://open.spotify.com/user/12122604372"
            },
            "href": "https://api.spotify.com/v1/users/12122604372",
            "id": "12122604372",
            "type": "user",
            "uri": "spotify:user:12122604372"
          },
          "primary_color": null,
          "public": false,
          "snapshot_id": "MyxjYWVlZjQwYzI1ZTUwYWYyZjNiMjVmOThlMGMyMWQ3NTUyZjJlZjMy",
          "tracks": {
            "href": "https://api.spotify.com/v1/playlists/4gH6yuY8CoFjqHlbPlxVM6/tracks?offset=0&limit=100&locale=en-US,en;q=0.9",
            "items": [],
            "limit": 100,
            "next": null,
            "offset": 0,
            "previous": null,
            "total": 0
          },
          "type": "playlist",
          "uri": "spotify:playlist:4gH6yuY8CoFjqHlbPlxVM6"
        }
        """;

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemJson)));
    // Then
    assertThat(underTest.getPlaylist(playlistId))
        .isNotNull()
        .isEqualTo(JsonHelper.jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class));
  }

  @Test
  void itShouldAddItemsToPlaylist() {
    // Given
    String playlistId = "4gH6yuY8CoFjqHlbPlxVM6";
    String addItemsResponseJson =
        """
        {"snapshot_id": "MywxZjVmNmIyYjYzZGZkMDdlZTQ3MDY3ZjBkOTMxNDY2ZTU0OWNjOTkw"}
        """;

    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(addItemsResponseJson)));
    // Then
    assertThat(
            underTest.addItemsToPlaylist(
                playlistId,
                new AddItemsRequest(
                    Collections.singletonList(URI.create("spotify:track:1NRrU8Lrsb5Jrcxk6UjJb3")))))
        .isNotNull()
        .isEqualTo(JsonHelper.jsonToObject(addItemsResponseJson, AddItemsResponse.class));
  }
}

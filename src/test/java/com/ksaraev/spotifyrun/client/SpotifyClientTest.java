package com.ksaraev.spotifyrun.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyBadRequestException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyException;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.AddItemsResponse;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.AssertionErrors;

import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@ActiveProfiles("test")
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
  void itShouldGetCurrentUserProfile() {
    // Given
    String responseBody =
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
              "url": "https://scontent-ams2-1.xx.fbcdn.net/v/t1.18169-1/18582217_756510051193142_2668224842211227445_n.jpg?stp=dst-jpg_p320x320&_nc_cat=107&ccb=1-7&_nc_sid=0c64ff&_nc_ohc=f9uHFAS3-NgAX_Jf_7W&_nc_ht=scontent-ams2-1.xx&edm=AP4hL3IEAAAA&oh=00_AfDz25uCayrtbFOIPhqa-aN1_up317o9l-yKVIk90bfQWA&oe=64095536",
              "width": null
            }
          ],
          "type": "user",
          "uri": "spotify:user:12122604372"
        }
        """;

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));

    // When and Then
    assertThat(underTest.getCurrentUserProfile())
        .isNotNull()
        .isEqualTo(responseBodyToObject(responseBody, SpotifyUserProfileItem.class));
  }

  @Test
  void itShouldGetUserTopTracks() {
    // Given
    String responseBody =
        """
        {
          "items": [
            {
              "album": {
                "album_type": "ALBUM",
                "artists": [
                  {
                    "external_urls": {
                      "spotify": "https://open.spotify.com/artist/2Tz1DTzVJ5Gyh8ZwVr6ekU"
                    },
                    "href": "https://api.spotify.com/v1/artists/2Tz1DTzVJ5Gyh8ZwVr6ekU",
                    "id": "2Tz1DTzVJ5Gyh8ZwVr6ekU",
                    "name": "STRFKR",
                    "type": "artist",
                    "uri": "spotify:artist:2Tz1DTzVJ5Gyh8ZwVr6ekU"
                  }
                ],
                "available_markets": [
                  "ZA"
                ],
                "external_urls": {
                  "spotify": "https://open.spotify.com/album/1bvGSdxElbk5CTTPIrWO2L"
                },
                "href": "https://api.spotify.com/v1/albums/1bvGSdxElbk5CTTPIrWO2L",
                "id": "1bvGSdxElbk5CTTPIrWO2L",
                "images": [
                  {
                    "height": 640,
                    "url": "https://i.scdn.co/image/ab67616d0000b2739bd8208f0a750a00d2bf8c8b",
                    "width": 640
                  },
                  {
                    "height": 300,
                    "url": "https://i.scdn.co/image/ab67616d00001e029bd8208f0a750a00d2bf8c8b",
                    "width": 300
                  },
                  {
                    "height": 64,
                    "url": "https://i.scdn.co/image/ab67616d000048519bd8208f0a750a00d2bf8c8b",
                    "width": 64
                  }
                ],
                "name": "Miracle Mile",
                "release_date": "2013-02-19",
                "release_date_precision": "day",
                "total_tracks": 15,
                "type": "album",
                "uri": "spotify:album:1bvGSdxElbk5CTTPIrWO2L"
              },
              "artists": [
                {
                  "external_urls": {
                    "spotify": "https://open.spotify.com/artist/2Tz1DTzVJ5Gyh8ZwVr6ekU"
                  },
                  "href": "https://api.spotify.com/v1/artists/2Tz1DTzVJ5Gyh8ZwVr6ekU",
                  "id": "2Tz1DTzVJ5Gyh8ZwVr6ekU",
                  "name": "STRFKR",
                  "type": "artist",
                  "uri": "spotify:artist:2Tz1DTzVJ5Gyh8ZwVr6ekU"
                }
              ],
              "available_markets": [
                "US",
                "ZA"
              ],
              "disc_number": 1,
              "duration_ms": 283053,
              "explicit": false,
              "external_ids": {
                "isrc": "US3R41324814"
              },
              "external_urls": {
                "spotify": "https://open.spotify.com/track/1rPYEWQfIRo5A4fvLgFbBe"
              },
              "href": "https://api.spotify.com/v1/tracks/1rPYEWQfIRo5A4fvLgFbBe",
              "id": "1rPYEWQfIRo5A4fvLgFbBe",
              "is_local": false,
              "name": "Golden Light",
              "popularity": 55,
              "preview_url": "https://p.scdn.co/mp3-preview/94e48ea105188132cc416e0c549ed511846847c8?cid=774b29d4f13844c495f206cafdad9c86",
              "track_number": 14,
              "type": "track",
              "uri": "spotify:track:1rPYEWQfIRo5A4fvLgFbBe"
            }
          ],
          "total": 50,
          "limit": 1,
          "offset": 0,
          "href": "https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term",
          "next": "https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1&time_range=short_term",
          "previous": null
        }
        """;

    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));

    // When and Then
    assertThat(
            underTest.getUserTopTracks(
                new GetUserTopTracksRequest(1, 0, GetUserTopTracksRequest.TimeRange.MEDIUM_TERM)))
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(responseBodyToObject(responseBody, GetUserTopTracksResponse.class));
  }

  @Test
  void itShouldGetRecommendations() {
    // Given
    String responseBody =
        """
        {
          "tracks": [
            {
              "album": {
                "album_type": "ALBUM",
                "artists": [
                  {
                    "external_urls": {
                      "spotify": "https://open.spotify.com/artist/75dQReiBOHN37fQgWQrIAJ"
                    },
                    "href": "https://api.spotify.com/v1/artists/75dQReiBOHN37fQgWQrIAJ",
                    "id": "75dQReiBOHN37fQgWQrIAJ",
                    "name": "Local Natives",
                    "type": "artist",
                    "uri": "spotify:artist:75dQReiBOHN37fQgWQrIAJ"
                  }
                ],
                "available_markets": [
                  "AD"],
                "external_urls": {
                  "spotify": "https://open.spotify.com/album/0fotfoDi4S3jE6qH2X4NSP"
                },
                "href": "https://api.spotify.com/v1/albums/0fotfoDi4S3jE6qH2X4NSP",
                "id": "0fotfoDi4S3jE6qH2X4NSP",
                "images": [
                  {
                    "height": 640,
                    "url": "https://i.scdn.co/image/ab67616d0000b2737f12907b6316ba19299e90fa",
                    "width": 640
                  },
                  {
                    "height": 300,
                    "url": "https://i.scdn.co/image/ab67616d00001e027f12907b6316ba19299e90fa",
                    "width": 300
                  },
                  {
                    "height": 64,
                    "url": "https://i.scdn.co/image/ab67616d000048517f12907b6316ba19299e90fa",
                    "width": 64
                  }
                ],
                "name": "Gorilla Manor",
                "release_date": "2009-11-02",
                "release_date_precision": "day",
                "total_tracks": 12,
                "type": "album",
                "uri": "spotify:album:0fotfoDi4S3jE6qH2X4NSP"
              },
              "artists": [
                {
                  "external_urls": {
                    "spotify": "https://open.spotify.com/artist/75dQReiBOHN37fQgWQrIAJ"
                  },
                  "href": "https://api.spotify.com/v1/artists/75dQReiBOHN37fQgWQrIAJ",
                  "id": "75dQReiBOHN37fQgWQrIAJ",
                  "name": "Local Natives",
                  "type": "artist",
                  "uri": "spotify:artist:75dQReiBOHN37fQgWQrIAJ"
                }
              ],
              "available_markets": [
                "ZW"
              ],
              "disc_number": 1,
              "duration_ms": 266440,
              "explicit": false,
              "external_ids": {
                "isrc": "GBZUZ0900064"
              },
              "external_urls": {
                "spotify": "https://open.spotify.com/track/5BvTfZQrTgu4buyXOekT3F"
              },
              "href": "https://api.spotify.com/v1/tracks/5BvTfZQrTgu4buyXOekT3F",
              "id": "5BvTfZQrTgu4buyXOekT3F",
              "is_local": false,
              "name": "Wide Eyes",
              "popularity": 41,
              "preview_url": "https://p.scdn.co/mp3-preview/a60b97bf85867b810d64b1a92f1ec730bf509123?cid=774b29d4f13844c495f206cafdad9c86",
              "track_number": 1,
              "type": "track",
              "uri": "spotify:track:5BvTfZQrTgu4buyXOekT3F"
            }
          ],
          "seeds": [
            {
              "initialPoolSize": 0,
              "afterFilteringSize": 0,
              "afterRelinkingSize": 0,
              "id": "synth pop",
              "type": "GENRE",
              "href": null
            },
            {
              "initialPoolSize": 500,
              "afterFilteringSize": 138,
              "afterRelinkingSize": 138,
              "id": "2Tz1DTzVJ5Gyh8ZwVr6ekU",
              "type": "ARTIST",
              "href": "https://api.spotify.com/v1/artists/2Tz1DTzVJ5Gyh8ZwVr6ekU"
            },
            {
              "initialPoolSize": 500,
              "afterFilteringSize": 134,
              "afterRelinkingSize": 134,
              "id": "1rPYEWQfIRo5A4fvLgFbBe",
              "type": "TRACK",
              "href": "https://api.spotify.com/v1/tracks/1rPYEWQfIRo5A4fvLgFbBe"
            }
          ]
        }
        """;

    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));

    // When and Then
    assertThat(
            underTest.getRecommendations(
                new GetRecommendationsRequest(
                    Collections.singletonList("2Tz1DTzVJ5Gyh8ZwVr6ekU"),
                    Collections.singletonList("synth pop"),
                    Collections.singletonList("1rPYEWQfIRo5A4fvLgFbBe"),
                    null,
                    1,
                    0)))
        .isNotNull()
        .isEqualTo(responseBodyToObject(responseBody, GetRecommendationsResponse.class));
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    String userId = "12122604372";
    String responseBody =
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
                    .withBody(responseBody)));

    // When and Then
    assertThat(
            underTest.createPlaylist(
                userId,
                new SpotifyPlaylistItemDetails(
                    false, false, "New Playlist", "New Playlist description")))
        .isNotNull()
        .isEqualTo(responseBodyToObject(responseBody, SpotifyPlaylistItem.class));
  }

  @Test
  void itShouldGetPlaylist() {
    // Given
    String playlistId = "4gH6yuY8CoFjqHlbPlxVM6";
    String responseBody =
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
                    .withBody(responseBody)));

    // When and Then
    assertThat(underTest.getPlaylist(playlistId))
        .isNotNull()
        .isEqualTo(responseBodyToObject(responseBody, SpotifyPlaylistItem.class));
  }

  @Test
  void itShouldAddItemsToPlaylist() {
    // Given
    String playlistId = "4gH6yuY8CoFjqHlbPlxVM6";
    String responseBody =
        """
        {"snapshot_id": "MywxZjVmNmIyYjYzZGZkMDdlZTQ3MDY3ZjBkOTMxNDY2ZTU0OWNjOTkw"}
        """;

    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));

    // When and Then
    assertThat(
            underTest.addItemsToPlaylist(
                playlistId,
                new AddItemsRequest(
                    Collections.singletonList(URI.create("spotify:track:1NRrU8Lrsb5Jrcxk6UjJb3")))))
        .isNotNull()
        .isEqualTo(responseBodyToObject(responseBody, AddItemsResponse.class));
  }

  @Test
  void itShouldThrowSpotifyExceptionWithOriginalErrorMessageWhenReceiveHttpErrorStatusCode() {
    // Given
    String errorMessage =
        """
        {
            "error": {
                "status": 400,
                "message": "Bad Request"
            }
        }""";

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(errorMessage)
                    .withStatus(400)));
    // When and Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isInstanceOf(SpotifyBadRequestException.class)
        .hasMessage(errorMessage);
  }

  @ParameterizedTest
  @CsvSource({
    "400, SpotifyBadRequestException",
    "401, SpotifyUnauthorizedException",
    "403, SpotifyForbiddenException",
    "404, SpotifyNotFoundException",
    "429, SpotifyTooManyRequestsException",
    "500, SpotifyInternalServerErrorException",
    "502, SpotifyBadGatewayException",
    "503, SpotifyServiceUnavailableException",
  })
  void itShouldThrowSpotifyExceptionWhenReceiveHttpErrorStatusCode(Integer status, String className)
      throws Exception {
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withStatus(status)));

    // When and Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isInstanceOf(Class.forName("com.ksaraev.spotifyrun.client.exception.http." + className));
  }

  @Test
  void itShouldThrowSpotifyExceptionWhenReceiveHttpErrorStatusCodeAndDontHavePredefinedException() {
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/v1/me"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withStatus(422)));

    // When and Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isInstanceOf(SpotifyException.class);
  }

  private <T> T responseBodyToObject(String responseBody, Class<T> aClass) {
    try {
      return new ObjectMapper().readValue(responseBody, aClass);
    } catch (JsonProcessingException e) {
      AssertionErrors.fail(
          "Fail to convert response body ["
              + responseBody
              + "] to instance of ["
              + aClass
              + "]:"
              + e.getMessage());
      return null;
    }
  }
}

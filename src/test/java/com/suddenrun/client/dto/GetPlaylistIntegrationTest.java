package com.suddenrun.client.dto;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.suddenrun.utils.JsonHelper;
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
class GetPlaylistIntegrationTest {

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
  void itShouldGetPlaylist() {
    // Given
    String spotifyPlaylistItemJson =
        """
                {
                  "collaborative": false,
                  "description": "description",
                  "external_urls": {
                    "spotify": "https://open.spotify.com/playlist/0moWPCTPTShumonjlsDgLe"
                  },
                  "followers": {
                    "href": null,
                    "total": 0
                  },
                  "href": "https://api.spotify.com/v1/playlists/0moWPCTPTShumonjlsDgLe",
                  "id": "0moWPCTPTShumonjlsDgLe",
                  "images": [
                    {
                      "height": 640,
                      "url": "https://i.scdn.co/image/1",
                      "width": 640
                    }
                  ],
                  "name": "playlist name",
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
                  "snapshot_id": "MywyM2Y2Zjg5YTdlNGQ3MmI2OGFiN2NiZmQ4NTNlZDdlMjE2OTFjODM4",
                  "tracks": {
                    "href": "https://api.spotify.com/v1/playlists/0moWPCTPTShumonjlsDgLe/tracks?offset=0&limit=100&locale=en-US,en;q=0.9",
                    "items": [
                      {
                        "added_at": "2023-03-03T12:31:57Z",
                        "added_by": {
                          "external_urls": {
                            "spotify": "https://open.spotify.com/user/12122604372"
                          },
                          "href": "https://api.spotify.com/v1/users/12122604372",
                          "id": "12122604372",
                          "type": "user",
                          "uri": "spotify:user:12122604372"
                        },
                        "is_local": false,
                        "primary_color": null,
                        "track": {
                          "album": {
                            "album_type": "album",
                            "artists": [
                              {
                                "external_urls": {
                                  "spotify": "https://open.spotify.com/artist/012345012345AABBccDDee"
                                },
                                "href": "https://api.spotify.com/v1/artists/012345012345AABBccDDee",
                                "id": "012345012345AABBccDDee",
                                "name": "artist name",
                                "type": "artist",
                                "uri": "spotify:artist:012345012345AABBccDDee"
                              }
                            ],
                            "available_markets": [
                              "AD"
                            ],
                            "external_urls": {
                              "spotify": "https://open.spotify.com/album/002233442345AABBccDDee"
                            },
                            "href": "https://api.spotify.com/v1/albums/002233442345AABBccDDee",
                            "id": "002233442345AABBccDDee",
                            "images": [
                              {
                                "height": 640,
                                "url": "https://i.scdn.co/image/1",
                                "width": 640
                              },
                              {
                                "height": 300,
                                "url": "https://i.scdn.co/image/2",
                                "width": 300
                              },
                              {
                                "height": 64,
                                "url": "https://i.scdn.co/image/3",
                                "width": 64
                              }
                            ],
                            "name": "album name",
                            "release_date": "2021-04-20",
                            "release_date_precision": "day",
                            "total_tracks": 8,
                            "type": "album",
                            "uri": "spotify:album:002233442345AABBccDDee"
                          },
                          "artists": [
                            {
                              "external_urls": {
                                "spotify": "https://open.spotify.com/artist/012345012345AABBccDDee"
                              },
                              "href": "https://api.spotify.com/v1/artists/012345012345AABBccDDee",
                              "id": "012345012345AABBccDDee",
                              "name": "French Police",
                              "type": "artist",
                              "uri": "spotify:artist:012345012345AABBccDDee"
                            }
                          ],
                          "available_markets": [
                            "XK"
                          ],
                          "disc_number": 1,
                          "duration_ms": 136777,
                          "episode": false,
                          "explicit": false,
                          "external_ids": {
                            "isrc": "DD0011111111"
                          },
                          "external_urls": {
                            "spotify": "https://open.spotify.com/track/ZZBBcc442345AABBccDDee"
                          },
                          "href": "https://api.spotify.com/v1/tracks/ZZBBcc442345AABBccDDee",
                          "id": "ZZBBcc442345AABBccDDee",
                          "is_local": false,
                          "name": "track name",
                          "popularity": 48,
                          "preview_url": "https://p.scdn.co/mp3-preview/1?cid=2",
                          "track": true,
                          "track_number": 5,
                          "type": "track",
                          "uri": "spotify:track:ZZBBcc442345AABBccDDee"
                        },
                        "video_thumbnail": {
                          "url": null
                        }
                      }
                    ],
                    "limit": 100,
                    "next": null,
                    "offset": 0,
                    "previous": null,
                    "total": 1
                  },
                  "type": "playlist",
                  "uri": "spotify:playlist:0moWPCTPTShumonjlsDgLe"
                }
                """;

    String playlistId = "0moWPCTPTShumonjlsDgLe";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(jsonResponse(spotifyPlaylistItemJson, 200)));

    // Then
    assertThat(underTest.getPlaylist(playlistId))
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(JsonHelper.jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistDto.class));
  }
}

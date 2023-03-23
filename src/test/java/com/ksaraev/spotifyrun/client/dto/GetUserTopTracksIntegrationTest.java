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
class GetUserTopTracksIntegrationTest {

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
  void itShouldGetUserTopTracks() {
    // Given
    String getUserTopTracksResponseJson =
        """
        {
          "items":[
            {
              "album":{
                "album_type":"SINGLE",
                "artists":[
                  {
                    "external_urls":{
                      "spotify":"https://open.spotify.com/artist/1234567890AaBbCcDdEeFfG"
                    },
                    "href":"https://api.spotify.com/v1/artists/1234567890AaBbCcDdEeFfG",
                    "id":"1234567890AaBbCcDdEeFfG",
                    "name":"artist name",
                    "type":"artist",
                    "uri":"spotify:artist:1234567890AaBbCcDdEeFfG"
                  }
                ],
                "available_markets":[
                  "AD"
                ],
                "external_urls":{
                  "spotify":"https://open.spotify.com/album/0987654321AaBbCcDdEeFfG"
                },
                "href":"https://api.spotify.com/v1/albums/0987654321AaBbCcDdEeFfG",
                "id":"0987654321AaBbCcDdEeFfG",
                "images":[
                  {
                    "height":640,
                    "url":"https://i.scdn.co/image/1",
                    "width":640
                  },
                  {
                    "height":300,
                    "url":"https://i.scdn.co/image/2",
                    "width":300
                  },
                  {
                    "height":64,
                    "url":"https://i.scdn.co/image/3",
                    "width":64
                  }
                ],
                "name":"album name",
                "release_date":"2023-01-01",
                "release_date_precision":"day",
                "total_tracks":1,
                "type":"album",
                "uri":"spotify:album:0987654321AaBbCcDdEeFfG"
              },
              "artists":[
                {
                  "external_urls":{
                    "spotify":"https://open.spotify.com/artist/1234567890AaBbCcDdEeFfG"
                  },
                  "href":"https://api.spotify.com/v1/artists/1234567890AaBbCcDdEeFfG",
                  "id":"1234567890AaBbCcDdEeFfG",
                  "name":"artist name",
                  "type":"artist",
                  "uri":"spotify:artist:1234567890AaBbCcDdEeFfG"
                }
              ],
              "available_markets":[
                "AD"
              ],
              "disc_number":1,
              "duration_ms":220000,
              "explicit":false,
              "external_ids":{
                "isrc":"CCC012345678"
              },
              "external_urls":{
                "spotify":"https://open.spotify.com/track/112233445AaBbCcDdEeFfG"
              },
              "href":"https://api.spotify.com/v1/tracks/112233445AaBbCcDdEeFfG",
              "id":"112233445AaBbCcDdEeFfG",
              "is_local":false,
              "name":"track name",
              "popularity":51,
              "preview_url":"https://p.scdn.co/mp3-preview/1?cid=1",
              "track_number":1,
              "type":"track",
              "uri":"spotify:track:112233445AaBbCcDdEeFfG"
            }
          ],
          "total":50,
          "limit":1,
          "offset":0,
          "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term",
          "next":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1&time_range=short_term",
          "previous":null
        }
        """;

    Integer limit = 1;
    Integer offset = 0;
    GetUserTopTracksRequest.TimeRange timeRange = GetUserTopTracksRequest.TimeRange.SHORT_TERM;

    GetUserTopTracksRequest getUserTopTracksRequest =
        GetUserTopTracksRequest.builder().timeRange(timeRange).limit(limit).offset(offset).build();

    stubFor(
        get(urlEqualTo(
                "/v1/me/top/tracks?offset="
                    + offset
                    + "&time_range="
                    + timeRange.getTerm()
                    + "&limit="
                    + limit))
            .willReturn(jsonResponse(getUserTopTracksResponseJson, 200)));

    // Then
    assertThat(underTest.getUserTopTracks(getUserTopTracksRequest))
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(jsonToObject(getUserTopTracksResponseJson, GetUserTopTracksResponse.class));
  }
}

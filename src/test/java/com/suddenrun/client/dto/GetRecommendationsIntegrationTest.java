package com.suddenrun.client.dto;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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
class GetRecommendationsIntegrationTest {

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
  void itShouldGetRecommendations() {
    // Given
    String getRecommendationsResponseJson =
        """
        {
          "tracks":[
            {
              "album":{
                "album_type":"ALBUM",
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
                "release_date":"2023-02-14",
                "release_date_precision":"day",
                "total_tracks":10,
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
              "disc_number":1,
              "duration_ms":256915,
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
              "is_playable":true,
              "name":"track name",
              "popularity":18,
              "preview_url":"https://p.scdn.co/mp3-preview/1?cid=2",
              "track_number":6,
              "type":"track",
              "uri":"spotify:track:112233445AaBbCcDdEeFfG"
            }
          ],
          "seeds":[
            {
              "initialPoolSize":428,
              "afterFilteringSize":238,
              "afterRelinkingSize":238,
              "id":"0000567890AaBbCcDdEeFfG",
              "type":"ARTIST",
              "href":"https://api.spotify.com/v1/artists/0000567890AaBbCcDdEeFfG"
            },
            {
              "initialPoolSize":425,
              "afterFilteringSize":222,
              "afterRelinkingSize":222,
              "id":"112233445AaBbCcDdEeFfG",
              "type":"TRACK",
              "href":"https://api.spotify.com/v1/tracks/1122AA4450011CcDdEeFfG"
            },
            {
              "initialPoolSize":160,
              "afterFilteringSize":58,
              "afterRelinkingSize":58,
              "id":"genre name",
              "type":"GENRE",
              "href":null
            }
          ]
        }
        """;

    String artistId = "0000567890AaBbCcDdEeFfG";
    String trackId = "1122AA4450011CcDdEeFfG";
    String genreName = "name";

    List<String> seedArtistIds = Collections.singletonList(artistId);
    List<String> seedTracksIds = Collections.singletonList(trackId);
    List<String> seedGenres = Collections.singletonList(genreName);

    BigDecimal minTempo = new BigDecimal(120);

    GetRecommendationsRequest.TrackFeatures trackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

    Integer limit = 1;

    GetRecommendationsRequest getRecommendationsRequest =
        GetRecommendationsRequest.builder()
            .seedArtistIds(seedArtistIds)
            .seedTrackIds(seedTracksIds)
            .seedGenres(seedGenres)
            .trackFeatures(trackFeatures)
            .limit(limit)
            .build();

    stubFor(
        get(urlEqualTo(
                "/v1/recommendations?min_tempo="
                    + minTempo
                    + "&seed_tracks="
                    + trackId
                    + "&limit="
                    + limit
                    + "&seed_artists="
                    + artistId
                    + "&seed_genres="
                    + genreName))
            .willReturn(jsonResponse(getRecommendationsResponseJson, 200)));

    // Then
    assertThat(underTest.getRecommendations(getRecommendationsRequest))
        .isNotNull()
        .isEqualTo(JsonHelper.jsonToObject(getRecommendationsResponseJson, GetRecommendationsResponse.class));
  }
}

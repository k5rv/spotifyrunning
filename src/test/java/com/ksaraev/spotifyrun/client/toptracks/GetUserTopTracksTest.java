package com.ksaraev.spotifyrun.client.toptracks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
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
class GetUserTopTracksTest {

  private static final String GET_USER_TOP_TRACKS_REQUEST_VALUE = "getUserTopTracks.request";
  private static final String GET_USER_TOP_TRACKS_RETURN_VALUE = "getUserTopTracks.<return value>";

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
        .isEqualTo(jsonToObject(getUserTopTracksResponseJson, GetUserTopTracksResponse.class));
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           51|0 |.limit: must be less than or equal to 50
           0 |0 |.limit: must be greater than or equal to 1
           2 |-1|.offset: must be greater than or equal to 0
           """)
  void itShouldThrowConstraintViolationExceptionWhenGetUserTopTracksRequestIsNotValid(
      Integer limit, Integer offset, String message) {
    // Given
    GetUserTopTracksRequest getUserTopTracksRequest =
        GetUserTopTracksRequest.builder().limit(limit).offset(offset).build();

    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks(getUserTopTracksRequest))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_USER_TOP_TRACKS_REQUEST_VALUE + message);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenGetUserTopTracksRequestIsNull() {
    // Given
    String message = ": must not be null";

    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks(null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_USER_TOP_TRACKS_REQUEST_VALUE + message);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenGetUserTopTracksResponseIsNull() {
    // Given
    String message = ": must not be null";
    GetUserTopTracksRequest getUserTopTracksRequest = GetUserTopTracksRequest.builder().build();

    stubFor(get(urlEqualTo("/v1/me/top/tracks")).willReturn(jsonResponse(null, 200)));

    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks(getUserTopTracksRequest))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_USER_TOP_TRACKS_RETURN_VALUE + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           "key":"value"                |"name":"track name"|"uri":"spotify:track:112233445AaBbCcDdEeFfG"|"popularity":51 |TRUE |.trackItems[0].id: must not be null
           "id":"112233445AaBbCcDdEeFfG"|"key":"value"      |"uri":"spotify:track:112233445AaBbCcDdEeFfG"|"popularity":51 |TRUE |.trackItems[0].name: must not be empty
           "id":"112233445AaBbCcDdEeFfG"|"name":"track name"|"key":"value"                               |"popularity":51 |TRUE |.trackItems[0].uri: must not be null
           "id":"112233445AaBbCcDdEeFfG"|"name":"track name"|"uri":"spotify:track:112233445AaBbCcDdEeFfG"|"popularity":-1 |TRUE |.trackItems[0].popularity: must be greater than or equal to 0
           "id":"112233445AaBbCcDdEeFfG"|"name":"track name"|"uri":"spotify:track:112233445AaBbCcDdEeFfG"|"popularity":101|TRUE |.trackItems[0].popularity: must be less than or equal to 100
           "id":"112233445AaBbCcDdEeFfG"|"name":"track name"|"uri":"spotify:track:112233445AaBbCcDdEeFfG"|"popularity":51 |FALSE|.trackItems[0].artistItems: must not be empty
           """)
  void itShouldThrowConstraintViolationExceptionWhenGetUserTopTracksResponseTrackItemIsNotValid(
      String trackIdJsonKeyValue,
      String trackNameJsonKeyValue,
      String trackUriJsonKeyValue,
      String trackPopularityJsonKeyValue,
      Boolean trackHasArtists,
      String message) {
    // Given
    String trackArtistsJsonKeyValue =
        trackHasArtists
            ? """
                  "artists":[
                    {
                      "external_urls":{
                        "spotify":"https://open.spotify.com/artist/1234567890AaBbCcDdEeFfG"
                      },
                      "href":"https://api.spotify.com/v1/artists/1234567890AaBbCcDdEeFfG",
                      "id":"1234567890AaBbCcDdEeFfG",
                      "name":"artist name",
                      "uri":"spotify:artist:1234567890AaBbCcDdEeFfG",
                      "type":"artist"
                    }
                  ]
                  """
            : """
                  "artists":[]
                   """;

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
              %s,
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
              "is_local":false,
              %s,
              %s,
              %s,
              %s,
              "preview_url":"https://p.scdn.co/mp3-preview/1?cid=1",
              "track_number":1,
              "type":"track"
            }
          ],
          "total":50,
          "limit":1,
          "offset":0,
          "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term",
          "next":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1&time_range=short_term",
          "previous":null
        }
        """
            .formatted(
                trackIdJsonKeyValue,
                trackNameJsonKeyValue,
                trackUriJsonKeyValue,
                trackPopularityJsonKeyValue,
                trackArtistsJsonKeyValue);

    GetUserTopTracksRequest getUserTopTracksRequest = GetUserTopTracksRequest.builder().build();

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(jsonResponse(getUserTopTracksResponseJson, 200)));

    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks(getUserTopTracksRequest))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_USER_TOP_TRACKS_RETURN_VALUE + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           "key":"value"                 |"name":"artist name"|"uri":"spotify:artist:1234567890AaBbCcDdEeFfG"|.trackItems[0].artistItems[0].id: must not be null
           "id":"1234567890AaBbCcDdEeFfG"|"key":"value"       |"uri":"spotify:artist:1234567890AaBbCcDdEeFfG"|.trackItems[0].artistItems[0].name: must not be empty
           "id":"1234567890AaBbCcDdEeFfG"|"name":"artist name"|"key":"value"                                 |.trackItems[0].artistItems[0].uri: must not be null
           """)
  void
      itShouldThrowConstraintViolationExceptionWhenGetUserTopTracksResponseTrackItemArtistItemIsNotValid(
          String artistIdJsonKeyValue,
          String artistNameJsonKeyValue,
          String artistUriJsonKeyValue,
          String message) {
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
                          %s,
                          %s,
                          %s,
                          "type":"artist"
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
                """
            .formatted(artistIdJsonKeyValue, artistNameJsonKeyValue, artistUriJsonKeyValue);

    GetUserTopTracksRequest getUserTopTracksRequest = GetUserTopTracksRequest.builder().build();

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(jsonResponse(getUserTopTracksResponseJson, 200)));

    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks(getUserTopTracksRequest))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(GET_USER_TOP_TRACKS_RETURN_VALUE + message);
  }
}

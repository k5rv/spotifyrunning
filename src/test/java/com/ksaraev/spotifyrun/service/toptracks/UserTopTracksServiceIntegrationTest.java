package com.ksaraev.spotifyrun.service.toptracks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.exception.GetUserTopTracksException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.service.UserTopTracksService;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.exception.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON_UTF8;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserTopTracksServiceIntegrationTest {

  public static WireMockServer mock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired private UserTopTracksService underTest;

  @BeforeAll
  static void setupClass() {
    mock.start();
  }

  @AfterAll
  static void clean() {
    mock.shutdown();
  }

  @AfterEach
  void after() {
    mock.resetAll();
  }

  @Test
  void itShouldReturnTracks() {
    // Given
    String artistId = "5VnrXCUzaatWXs702ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    SpotifyArtist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();
    String trackId = "5Ko5Jn0OJ8IJFMHhSYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OJ8IJFMHhSYsCnj");
    int trackPopularity = 44;
    SpotifyTrack track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();
    String getUserTopTracksResponseJson =
        """
            {
              "items":[
                {
                  "album":{
                    "album_type":"ALBUM",
                    "artists":[
                      {
                        "external_urls":{
                          "spotify":"https://open.spotify.com/artist/5VnrXCUzaatWXs702ScGwN"
                        },
                        "href":"https://api.spotify.com/v1/artists/5VnrXCUzaatWXs702ScGwN",
                        "id":"5VnrXCUzaatWXs702ScGwN",
                        "name":"name",
                        "type":"artist",
                        "uri":"spotify:artist:5VnrXCUzaatWXs702ScGwN"
                      }
                    ],
                    "available_markets":[
                      "AD"
                    ],
                    "external_urls":{
                      "spotify":"https://open.spotify.com/album/0t58MEWaEcEOfp5aelHocT"
                    },
                    "href":"https://api.spotify.com/v1/albums/0t58MEWaEcEOfp5aelHocT",
                    "id":"0t58MEWaEcEOfp5aelHocT",
                    "images":[
                      {
                        "height":640,
                        "url":"https://i.scdn.co/image/Ab67116d0000b2734b8b7ed8a19cef47f4438944",
                        "width":640
                      },
                      {
                        "height":300,
                        "url":"https://i.scdn.co/image/Ab67116d0000b2734b8b7ed8a19cef47f4438944",
                        "width":300
                      },
                      {
                        "height":64,
                        "url":"https://i.scdn.co/image/Ab67116d0000b2734b8b7ed8a19cef47f4438944",
                        "width":64
                      }
                    ],
                    "name":"name",
                    "release_date":"2022-01-01",
                    "release_date_precision":"day",
                    "total_tracks":10,
                    "type":"album",
                    "uri":"spotify:album:0t58MEWaEcEOfp5aelHocT"
                  },
                  "artists":[
                    {
                      "external_urls":{
                        "spotify":"https://open.spotify.com/artist/5VnrXCUzaatWXs702ScGwN"
                      },
                      "href":"https://api.spotify.com/v1/artists/5VnrXCUzaatWXs702ScGwN",
                      "id":"%s",
                      "name":"%s",
                      "type":"artist",
                      "uri":"%s"
                    }
                  ],
                  "available_markets":[
                    "AD"
                  ],
                  "disc_number":1,
                  "duration_ms":239493,
                  "explicit":false,
                  "external_ids":{
                    "isrc":"CCEX32135501"
                  },
                  "external_urls":{
                    "spotify":"https://open.spotify.com/track/5Ko5Jn0OJ8IJFMHhSYsCnj"
                  },
                  "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OJ8IJFMHhSYsCnj",
                  "id":"%s",
                  "is_local":false,
                  "name":"%s",
                  "popularity":%s,
                  "preview_url":"https://p.scdn.co/mp3-preview/1234567890?cid=1234567890",
                  "track_number":9,
                  "type":"track",
                  "uri":"%s"
                }
              ],
              "total":50,
              "limit":1,
              "offset":0,
              "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0",
              "next":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1",
              "previous":null
            }
        """
            .formatted(
                artistId, artistName, artistUri, trackId, trackName, trackPopularity, trackUri);
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(getUserTopTracksResponseJson)));
    // Then
    Assertions.assertThat(underTest.getUserTopTracks()).containsExactly(track);
  }

  @Test
  void itShouldReturnEmptyListWhenTrackItemsAreEmpty() {
    // Given
    String getUserTopTracksResponseJson =
        """
             {
                "items":[],
                "total":0,
                "limit":0,
                "offset":0,
                "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0",
                "next":null,
                "previous":null
             }
             """;
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(getUserTopTracksResponseJson)));
    // Then
    Assertions.assertThat(underTest.getUserTopTracks()).isEmpty();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               "key":"value",                |"name":"name",|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj",|"popularity":32 |FALSE|id: must not be null
               "id":"5Ko5Jn0OG8IDFEHhAYsCnj",|"key":"value",|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj",|"popularity":32 |FALSE|name: must not be empty
               "id":"5Ko5Jn0OG8IDFEHhAYsCnj",|"name":"name",|"key":"value",                               |"popularity":32 |FALSE|uri: must not be null
               "id":"5Ko5Jn0OG8IDFEHhAYsCnj",|"name":"name",|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj",|"popularity":-1 |FALSE|popularity: must be greater than or equal to 0
               "id":"5Ko5Jn0OG8IDFEHhAYsCnj",|"name":"name",|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj",|"popularity":101|FALSE|popularity: must be less than or equal to 100
               "id":"5Ko5Jn0OG8IDFEHhAYsCnj",|"name":"name",|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj",|"popularity":32 |TRUE |artistItems: must not be empty
               """)
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyReturnsNotValidTrackItem(
      String idJsonKeyValue,
      String nameJsonKeyValue,
      String uriJsonKeyValue,
      String popularityJsonKeyValue,
      Boolean hasEmptyArtistsJsonKeyValue,
      String constraintViolationMessage) {
    // Given
    String artistsJsonKeyValue =
        hasEmptyArtistsJsonKeyValue
            ? """
                   "artists":[],"""
            : """
                   "artists":[
                     {
                       "external_urls":{
                         "spotify":"https://open.spotify.com/artist/5VnrVRXzAatWXr106ScGwN"
                       },
                       "href":"https://api.spotify.com/v1/artists/5VnrVRXzAatWXr106ScGwN",
                       "id":"5VnrVRXzAatWXr106ScGwN",
                       "name":"name",
                       "type":"artist",
                       "uri":"spotify:artist:5VnrVRXzAatWXr106ScGwN"
                     }
                   ],
                   """;

    String getUserTopTracksResponseJson =
        """
             {
               "items":[
                 {
                   "album":{
                     "album_type":"ALBUM",
                     "artists":[
                       {
                         "external_urls":{
                           "spotify":"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN"
                         },
                         "href":"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN",
                         "id":"5VnrVRYzaatWXs102ScGwN",
                         "name":"Artist Name",
                         "type":"artist",
                         "uri":"spotify:artist:5VnrVRYzaatWXs102ScGwN"
                       }
                     ],
                     "available_markets":[
                       "US"
                     ],
                     "external_urls":{
                       "spotify":"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU"
                     },
                     "href":"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU",
                     "id":"0t58MAAaEcFOfp5aeljocU",
                     "images":[
                       {
                         "height":640,
                         "url":"https://i.scdn.co/image/ab67616d0000b2734b8b7ed8a19cef47f4438913",
                         "width":640
                       },
                       {
                         "height":300,
                         "url":"https://i.scdn.co/image/ab67616d00001e024b8b7ed8a19cef47f4438913",
                         "width":300
                       },
                       {
                         "height":64,
                         "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                         "width":64
                       }
                     ],
                     "name":"Name",
                     "release_date":"2001-06-11",
                     "release_date_precision":"day",
                     "total_tracks":10,
                     "type":"album",
                     "uri":"spotify:album:0t58MAAaEcFOfp5aeljocU"
                   },
                   %s
                   "available_markets":[
                     "US"
                   ],
                   "disc_number":1,
                   "duration_ms":239493,
                   "explicit":false,
                   "external_ids":{
                     "isrc":"UKEX32135502"
                   },
                   "external_urls":{
                     "spotify":"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj"
                   },
                   "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj",
                   %s
                   "is_local":false,
                   %s
                   %s
                   "preview_url":"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86",
                   "track_number":9,
                   "type":"track",
                   %s
                 }
               ],
               "total":50,
               "limit":1,
               "offset":0,
               "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0",
               "next":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1",
               "previous":null
             };
             """
            .formatted(
                artistsJsonKeyValue,
                idJsonKeyValue,
                nameJsonKeyValue,
                uriJsonKeyValue,
                popularityJsonKeyValue);

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(getUserTopTracksResponseJson)));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(
            UNABLE_TO_GET_USER_TOP_TRACKS
                + "getUserTopTracks.<return value>.trackItems[0]."
                + constraintViolationMessage);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyReturnsNotValidGetUserTopTracksResponse() {
    // Given
    String message = "getUserTopTracks.<return value>.trackItems[1].id: must not be null";
    String spotifyTrackItemJson =
        """
              {
                "album":{
                  "album_type":"ALBUM",
                  "artists":[
                    {
                      "external_urls":{
                        "spotify":"https://open.spotify.com/artist/5VnrXCUzaatWXs702ScGwN"
                      },
                      "href":"https://api.spotify.com/v1/artists/5VnrXCUzaatWXs702ScGwN",
                      "id":"5VnrXCUzaatWXs702ScGwN",
                      "name":"name",
                      "type":"artist",
                      "uri":"spotify:artist:5VnrXCUzaatWXs702ScGwN"
                    }
                  ],
                  "available_markets":[
                    "AD"
                  ],
                  "external_urls":{
                    "spotify":"https://open.spotify.com/album/0t58MEWaEcEOfp5aelHocT"
                  },
                  "href":"https://api.spotify.com/v1/albums/0t58MEWaEcEOfp5aelHocT",
                  "id":"0t58MEWaEcEOfp5aelHocT",
                  "images":[
                    {
                      "height":640,
                      "url":"https://i.scdn.co/image/Ab67116d0000b2734b8b7ed8a19cef47f4438944",
                      "width":640
                    },
                    {
                      "height":300,
                      "url":"https://i.scdn.co/image/Ab67116d0000b2734b8b7ed8a19cef47f4438944",
                      "width":300
                    },
                    {
                      "height":64,
                      "url":"https://i.scdn.co/image/Ab67116d0000b2734b8b7ed8a19cef47f4438944",
                      "width":64
                    }
                  ],
                  "name":"name",
                  "release_date":"2022-01-01",
                  "release_date_precision":"day",
                  "total_tracks":10,
                  "type":"album",
                  "uri":"spotify:album:0t58MEWaEcEOfp5aelHocT"
                },
                "artists":[
                  {
                    "external_urls":{
                      "spotify":"https://open.spotify.com/artist/5VnrXCUzaatWXs702ScGwN"
                    },
                    "href":"https://api.spotify.com/v1/artists/5VnrXCUzaatWXs702ScGwN",
                    "id":"5VnrXCUzaatWXs702ScGwN",
                    "name":"name",
                    "type":"artist",
                    "uri":"spotify:artist:5VnrVRYzaatWXs102ScGwN"
                  }
                ],
                "available_markets":[
                  "AD"
                ],
                "disc_number":1,
                "duration_ms":239493,
                "explicit":false,
                "external_ids":{
                  "isrc":"CCEX32135501"
                },
                "external_urls":{
                  "spotify":"https://open.spotify.com/track/5Ko5Jn0OJ8IJFMHhSYsCnj"
                },
                "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OJ8IJFMHhSYsCnj",
                %s
                "is_local":false,
                "name":"name",
                "popularity":45,
                "preview_url":"https://p.scdn.co/mp3-preview/1234567890?cid=1234567890",
                "track_number":9,
                "type":"track",
                "uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"
              }
              """;

    String idJsonKeyValue = """
        "id":"5Ko5Jn0OG8IDFEHhAYsCnj",
        """;

    String getUserTopTracksResponseJson =
        """
             {
                "items":[%s,%s],
                "total":0,
                "limit":0,
                "offset":0,
                "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0",
                "next":null,
                "previous":null
             }
             """
            .formatted(
                spotifyTrackItemJson.formatted(idJsonKeyValue), spotifyTrackItemJson.formatted(""));

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(getUserTopTracksResponseJson)));

    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyReturnsEmptyResponseBody() {
    // Given
    String message = "getUserTopTracks.<return value>: must not be null";
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               {"error":{"status":401,"message":"Unauthorized"}}
               {"error":"invalid_client","error_description":"Invalid client secret"}
               plain text
               ""
               """)
  void itShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs401(String message) {
    // Given
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(401)));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               400|{"error":{"status":400,"message":"Bad Request"}},
               403|{"error":{"status":403,"message":"Forbidden"}},
               429|{"error":{"status":429,"message":"Too Many Requests"}},
               500|{"error":{"status":500,"message":"Internal Server Error"}},
               502|{"error":{"status":502,"message":"Bad Gateway"}},
               503|{"error":{"status":503,"message":"Service Unavailable"}},
               400|{"error":"invalid_client","error_description":"Invalid client secret"}
               400|plain text
               400|""
               """)
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyResponseHttpStatusCodeIsNot2XX(
      Integer status, String message) {
    // Given
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(status)));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
               {"id:"100",name":"something","size":"20"}
               Plain text
               """)
  void
      itShouldThrowGetUserTopTracksExceptionWhenHttpResponseBodyNotAJsonRepresentationOfGetUserTopTracksResponseClass(
          String responseBody) {
    // Given
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(
            UNABLE_TO_GET_USER_TOP_TRACKS
                + "Error while extracting response for type [class "
                + GetUserTopTracksResponse.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

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
import jakarta.validation.ConstraintViolationException;
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
  void itShouldGetUserTopTracks() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "Artist Name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    SpotifyArtist artist = new Artist(artistId, artistName, artistUri, null);

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "Track Name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    int trackPopularity = 32;
    SpotifyTrack track = new Track(trackId, trackName, trackUri, trackPopularity, List.of(artist));

    String responseBody =
        "{\n"
            + "  \"items\":[\n"
            + "    {\n"
            + "      \"album\":{\n"
            + "        \"album_type\":\"ALBUM\",\n"
            + "        \"artists\":[\n"
            + "          {\n"
            + "            \"external_urls\":{\n"
            + "              \"spotify\":\"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN\"\n"
            + "            },\n"
            + "            \"href\":\"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN\",\n"
            + "            \"id\":\""
            + artistId
            + "\",\n"
            + "            \"name\":\""
            + artistName
            + "\",\n"
            + "            \"type\":\"artist\",\n"
            + "            \"uri\":\""
            + artistUri
            + "\"\n"
            + "          }\n"
            + "        ],\n"
            + "        \"available_markets\":[\n"
            + "          \"US\",\n"
            + "          \"GB\"\n"
            + "        ],\n"
            + "        \"external_urls\":{\n"
            + "          \"spotify\":\"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU\"\n"
            + "        },\n"
            + "        \"href\":\"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU\",\n"
            + "        \"id\":\"0t58MAAaEcFOfp5aeljocU\",\n"
            + "        \"images\":[\n"
            + "          {\n"
            + "            \"height\":640,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":640\n"
            + "          },\n"
            + "          {\n"
            + "            \"height\":300,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":300\n"
            + "          },\n"
            + "          {\n"
            + "            \"height\":64,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":64\n"
            + "          }\n"
            + "        ],\n"
            + "        \"name\":\"Name\",\n"
            + "        \"release_date\":\"2001-06-11\",\n"
            + "        \"release_date_precision\":\"day\",\n"
            + "        \"total_tracks\":10,\n"
            + "        \"type\":\"album\",\n"
            + "        \"uri\":\"spotify:album:0t58MAAaEcFOfp5aeljocU\"\n"
            + "      },\n"
            + "      \"artists\":[\n"
            + "        {\n"
            + "          \"external_urls\":{\n"
            + "            \"spotify\":\"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN\"\n"
            + "          },\n"
            + "          \"href\":\"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN\",\n"
            + "          \"id\":\""
            + artistId
            + "\",\n"
            + "          \"name\":\""
            + artistName
            + "\",\n"
            + "          \"type\":\"artist\",\n"
            + "          \"uri\":\""
            + artistUri
            + "\"\n"
            + "        }\n"
            + "      ],\n"
            + "      \"available_markets\":[\n"
            + "        \"US\",\n"
            + "        \"GB\"\n"
            + "      ],\n"
            + "      \"disc_number\":1,\n"
            + "      \"duration_ms\":239493,\n"
            + "      \"explicit\":false,\n"
            + "      \"external_ids\":{\n"
            + "        \"isrc\":\"UKEX32135502\"\n"
            + "      },\n"
            + "      \"external_urls\":{\n"
            + "        \"spotify\":\"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj\"\n"
            + "      },\n"
            + "      \"href\":\"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj\",\n"
            + "      \"id\":\""
            + trackId
            + "\",\n"
            + "      \"is_local\":false,\n"
            + "      \"name\":\""
            + trackName
            + "\",\n"
            + "      \"popularity\":"
            + trackPopularity
            + ",\n"
            + "      \"preview_url\":\"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86\",\n"
            + "      \"track_number\":9,\n"
            + "      \"type\":\"track\",\n"
            + "      \"uri\":\""
            + trackUri
            + "\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"total\":50,\n"
            + "  \"limit\":1,\n"
            + "  \"offset\":0,\n"
            + "  \"href\":\"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0\",\n"
            + "  \"next\":\"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1\",\n"
            + "  \"previous\":null\n"
            + "}";

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // When and Then
    Assertions.assertThat(underTest.getUserTopTracks()).containsExactly(track);
  }

  @Test
  void itShouldReturnEmptyListWhenTopTracksAreNotPresent() {
    // Given
    String responseBody =
        """
               {
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
                    .withBody(responseBody)));
    // When and Then
    Assertions.assertThat(underTest.getUserTopTracks()).isEmpty();
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenTrackIdIsNotPresent() {
    // Given
    String responseBody =
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
                     "is_local":false,
                     "name":"Name Name",
                     "popularity":32,
                     "preview_url":"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86",
                     "track_number":9,
                     "type":"track",
                     "uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"
                   }
                 ],
                 "total":50,
                 "limit":1,
                 "offset":0,
                 "href":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0",
                 "next":"https://api.spotify.com/v1/me/top/tracks?limit=1&offset=1",
                 "previous":null
               }""";

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage("getUserTopTracks.<return value>[0].id: must not be null");
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
                  {"error":{"status":401,"message":"Unauthorized"}},
                  {"error":"invalid_client","error_description":"Invalid client secret"}
                  Plain text
                  ""
                  """,
      delimiter = '|')
  void itShouldThrowUnauthorizedExceptionWhenHttpStatusCode401(String responseBody) {
    // Given
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)
                    .withStatus(401)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + ": " + responseBody);
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
                  400|{"error":{"status":400,"message":"Bad Request"}}
                  403|{"error":{"status":403,"message":"Forbidden"}}
                  429|{"error":{"status":429,"message":"Too Many Requests"}}
                  500|{"error":{"status":500,"message":"Internal Server Error"}}
                  502|{"error":{"status":502,"message":"Bad Gateway"}}
                  503|{"error":{"status":503,"message":"Service Unavailable"}}
                  400|{"error":"invalid_client","error_description":"Invalid client secret"}
                  400|Plain text
                  400|""
                  """,
      delimiter = '|')
  void itShouldThrowGetUserTopTracksExceptionWhenHttpStatusCodeNot2XX(
      Integer status, String responseBody) {
    // Given
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)
                    .withStatus(status)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ": " + responseBody);
  }

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
                 {"id:"100",name":"something","size":"20"}
                 Plain text
                 """,
      delimiter = '|')
  void
      itShouldThrowGetUserTopTracksExceptionWhenHttpStatusCodeIs200AndResponseBodyIsNotGetUserTopTracksResponseJson(
          String responseBody) {
    // Given
    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(
                responseDefinition()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // When and Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(
            UNABLE_TO_GET_USER_TOP_TRACKS
                + ": "
                + "Error while extracting response for type [class "
                + GetUserTopTracksResponse.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

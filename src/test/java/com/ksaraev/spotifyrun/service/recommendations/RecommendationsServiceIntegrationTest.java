package com.ksaraev.spotifyrun.service.recommendations;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.exception.ForbiddenException;
import com.ksaraev.spotifyrun.exception.GetRecommendationsException;
import com.ksaraev.spotifyrun.exception.TooManyRequestsException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import com.ksaraev.spotifyrun.service.RecommendationsService;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
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

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.exception.ForbiddenException.FORBIDDEN;
import static com.ksaraev.spotifyrun.exception.GetRecommendationsException.UNABLE_TO_GET_RECOMMENDATIONS;
import static com.ksaraev.spotifyrun.exception.TooManyRequestsException.TOO_MANY_REQUESTS;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON_UTF8;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecommendationsServiceIntegrationTest {

  public static WireMockServer mock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired private RecommendationsService underTest;

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

  // getRecommendations.seedTracks: must not be null

  @Test
  void itShouldGetTracks() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    String getRecommendationsResponseJson =
        """
          {
            "tracks":[
              {
                "album":{
                  "album_type":"SINGLE",
                  "artists":[
                    {
                      "external_urls":{
                        "spotify":"https://open.spotify.com/artist/8ArsfFN0BmEeVYPzQEReRv"
                      },
                      "href":"https://api.spotify.com/v1/artists/8ArsfFN0BmEeVYPzQEReRv",
                      "id":"8ArsfFN0BmEeVYPzQEReRv",
                      "name":"name",
                      "type":"artist",
                      "uri":"spotify:artist:8ArsfFN0BmEeVYPzQEReRv"
                    }
                  ],
                  "available_markets":[
                    "AD"
                  ],
                  "external_urls":{
                    "spotify":"https://open.spotify.com/album/0Ta0Ro3omKEm0SRTxYX5ZX"
                  },
                  "href":"https://api.spotify.com/v1/albums/0Ta0Ro3omKEm0SRTxYX5ZX",
                  "id":"0Ta0Ro3omKEm0SRTxYX5ZX",
                  "images":[
                    {
                      "height":640,
                      "url":"https://i.scdn.co/image/1234567890",
                      "width":640
                    },
                    {
                      "height":300,
                      "url":"https://i.scdn.co/image/1234567890",
                      "width":300
                    },
                    {
                      "height":64,
                      "url":"https://i.scdn.co/image/1234567890",
                      "width":64
                    }
                  ],
                  "name":"name",
                  "release_date":"2022-01-01",
                  "release_date_precision":"day",
                  "total_tracks":1,
                  "type":"album",
                  "uri":"spotify:album:0Ta0Ro3omKEm0SRTxYX5ZX"
                },
                "artists":[
                  {
                    "external_urls":{
                      "spotify":"https://open.spotify.com/artist/8ArsfFN0BmEeVYPzQEReRv"
                    },
                    "href":"https://api.spotify.com/v1/artists/8ArsfFN0BmEeVYPzQEReRv",
                    "id":"%s",
                    "name":"%s",
                    "type":"artist",
                    "uri":"%s"
                  }
                ],
                "available_markets":[
                  "AR"
                ],
                "disc_number":1,
                "duration_ms":213030,
                "explicit":true,
                "external_ids":{
                  "isrc":"CC5KR0000100"
                },
                "external_urls":{
                  "spotify":"https://open.spotify.com/track/3ZRTh3V53QHNjJRCzwlAqh"
                },
                "href":"https://api.spotify.com/v1/tracks/3ZRTh3V53QHNjJRCzwlAqh",
                "id":"%s",
                "is_local":false,
                "name":"%s",
                "popularity":%s,
                "preview_url":"https://p.scdn.co/mp3-preview/cce282e15fecd2308ac7de6d3c1439bb8fffb304?cid=774b29d4f13844c495f206cafdad9c86",
                "track_number":1,
                "type":"track",
                "uri":"%s"
              }
            ],
            "seeds":[
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"synt-pop",
                "type":"GENRE",
                "href":null
              },
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"ARTIST",
                "href":"https://api.spotify.com/v1/artists/6J4LmfWAfxkEfQZE0OKCo6"
              },
              {
                "initialPoolSize":500,
                "afterFilteringSize":500,
                "afterRelinkingSize":500,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"TRACK",
                "href":"https://api.spotify.com/v1/tracks/6J4LmfWAfxkEfQZE0OKCo6"
              }
            ]
          }
          """
            .formatted(
                artistId, artistName, artistUri, trackId, trackName, trackPopularity, trackUri);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(getRecommendationsResponseJson)));

    // Then
    Assertions.assertThat(underTest.getRecommendations(List.of(track), trackFeatures))
        .containsExactly(track);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenSeedTracksIsEmpty() {
    // Then
    String message = "getRecommendations.seedTracks: size must be between 1 and 5";
    List<SpotifyTrack> seedTracks = List.of();
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenSeedTracksSizeGreaterThanFive() {
    // Given
    String message = "getRecommendations.seedTracks: size must be between 1 and 5";

    String artistId = "5VnrXCUzaatWXs702ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    SpotifyArtist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    SpotifyTrack track =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = new ArrayList<>();
    IntStream.rangeClosed(0, 5).forEach(i -> seedTracks.add(i, track));

    // Then
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenSeedTracksHasNullElements() {
    // Given
    String message = "getRecommendations.seedTracks[1].<list element>: must not be null";

    String artistId = "5VnrXCUzaatWXs702ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    SpotifyArtist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    SpotifyTrack track =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = new ArrayList<>();
    seedTracks.add(track);
    seedTracks.add(null);

    // Then
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenSeedTracksHasNotValidTrackItem() {
    // Given
    String message = "getRecommendations.seedTracks[1].id: must not be null";

    String artistId = "5VnrXCUzaatWXs702ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    SpotifyArtist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    SpotifyTrack trackOne =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(List.of(artist))
            .build();

    SpotifyTrack trackTwo =
        Track.builder()
            .id(null)
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(trackOne, trackTwo);

    // Then
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldThrowConstraintViolationExceptionWhenSeedTracksIsNull() {
    // Then
    String message = "getRecommendations.seedTracks: must not be null";
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(null, null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldReturnEmptyListWhenTrackItemsAreEmpty() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    String getRecommendationsResponseJson =
        """
          {
            "tracks":[],
            "seeds":[
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"synt-pop",
                "type":"GENRE",
                "href":null
              },
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"ARTIST",
                "href":"https://api.spotify.com/v1/artists/6J4LmfWAfxkEfQZE0OKCo6"
              },
              {
                "initialPoolSize":500,
                "afterFilteringSize":500,
                "afterRelinkingSize":500,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"TRACK",
                "href":"https://api.spotify.com/v1/tracks/6J4LmfWAfxkEfQZE0OKCo6"
              }
            ]
          }
          """;

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(getRecommendationsResponseJson)));

    // Then
    Assertions.assertThat(underTest.getRecommendations(seedTracks, trackFeatures)).isEmpty();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
            "key":"value"                |"name":"name"|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|"popularity":32 |FALSE|id: must not be null
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"key":"value"|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|"popularity":32 |FALSE|name: must not be empty
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"|"key":"value"                               |"popularity":32 |FALSE|uri: must not be null
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|"popularity":-1 |FALSE|popularity: must be greater than or equal to 0
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|"popularity":101|FALSE|popularity: must be less than or equal to 100
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|"popularity":32 |TRUE |artistItems: must not be empty
            """)
  void itShouldThrowGetRecommendationsExceptionWhenSpotifyReturnsNotValidTrackItem(
      String idJsonKeyValue,
      String nameJsonKeyValue,
      String uriJsonKeyValue,
      String popularityJsonKeyValue,
      Boolean hasEmptyArtistsJsonKeyValue,
      String constraintViolationMessage) {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    String artistsJsonKeyValue =
        hasEmptyArtistsJsonKeyValue
            ? """
               "artists":[]
               """
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
                 }]
               """;

    String getRecommendationsResponseJson =
        """
          {
            "tracks":[
              {
                "album":{
                  "album_type":"SINGLE",
                  "artists":[
                    {
                      "external_urls":{
                        "spotify":"https://open.spotify.com/artist/8ArsfFN0BmEeVYPzQEReRv"
                      },
                      "href":"https://api.spotify.com/v1/artists/8ArsfFN0BmEeVYPzQEReRv",
                      "id":"8ArsfFN0BmEeVYPzQEReRv",
                      "name":"name",
                      "type":"artist",
                      "uri":"spotify:artist:8ArsfFN0BmEeVYPzQEReRv"
                    }
                  ],
                  "available_markets":[
                    "AD"
                  ],
                  "external_urls":{
                    "spotify":"https://open.spotify.com/album/0Ta0Ro3omKEm0SRTxYX5ZX"
                  },
                  "href":"https://api.spotify.com/v1/albums/0Ta0Ro3omKEm0SRTxYX5ZX",
                  "id":"0Ta0Ro3omKEm0SRTxYX5ZX",
                  "images":[
                    {
                      "height":640,
                      "url":"https://i.scdn.co/image/1234567890",
                      "width":640
                    },
                    {
                      "height":300,
                      "url":"https://i.scdn.co/image/1234567890",
                      "width":300
                    },
                    {
                      "height":64,
                      "url":"https://i.scdn.co/image/1234567890",
                      "width":64
                    }
                  ],
                  "name":"name",
                  "release_date":"2022-01-01",
                  "release_date_precision":"day",
                  "total_tracks":1,
                  "type":"album",
                  "uri":"spotify:album:0Ta0Ro3omKEm0SRTxYX5ZX"
                },
                %s,
                "available_markets":[
                  "AR"
                ],
                "disc_number":1,
                "duration_ms":213030,
                "explicit":true,
                "external_ids":{
                  "isrc":"CC5KR0000100"
                },
                "external_urls":{
                  "spotify":"https://open.spotify.com/track/3ZRTh3V53QHNjJRCzwlAqh"
                },
                "href":"https://api.spotify.com/v1/tracks/3ZRTh3V53QHNjJRCzwlAqh",
                %s,
                "is_local":false,
                %s,
                %s,
                "preview_url":"https://p.scdn.co/mp3-preview/cce282e15fecd2308ac7de6d3c1439bb8fffb304?cid=774b29d4f13844c495f206cafdad9c86",
                "track_number":1,
                "type":"track",
                %s
              }
            ],
            "seeds":[
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"synt-pop",
                "type":"GENRE",
                "href":null
              },
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"ARTIST",
                "href":"https://api.spotify.com/v1/artists/6J4LmfWAfxkEfQZE0OKCo6"
              },
              {
                "initialPoolSize":500,
                "afterFilteringSize":500,
                "afterRelinkingSize":500,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"TRACK",
                "href":"https://api.spotify.com/v1/tracks/6J4LmfWAfxkEfQZE0OKCo6"
              }
            ]
          }
          """
            .formatted(
                artistsJsonKeyValue,
                idJsonKeyValue,
                nameJsonKeyValue,
                uriJsonKeyValue,
                popularityJsonKeyValue);

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(getRecommendationsResponseJson)));

    // Then
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(
            UNABLE_TO_GET_RECOMMENDATIONS
                + "getRecommendations.<return value>.trackItems[0]."
                + constraintViolationMessage);
  }

  @Test
  void
      itShouldThrowGetRecommendationsExceptionWhenSpotifyReturnsNotValidGeRecommendationsResponse() {
    // Given
    String message = "getRecommendations.<return value>.trackItems[1].id: must not be null";

    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

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
        "id":"5Ko5Jn0OG8IDFEHhAYsCnj",""";

    String getRecommendationsResponseJson =
        """
         {
            "tracks":[%s,%s],
            "seeds":[
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"synt-pop",
                "type":"GENRE",
                "href":null
              },
              {
                "initialPoolSize":0,
                "afterFilteringSize":0,
                "afterRelinkingSize":0,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"ARTIST",
                "href":"https://api.spotify.com/v1/artists/6J4LmfWAfxkEfQZE0OKCo6"
              },
              {
                "initialPoolSize":500,
                "afterFilteringSize":500,
                "afterRelinkingSize":500,
                "id":"6J4LmfWAfxkEfQZE0OKCo6",
                "type":"TRACK",
                "href":"https://api.spotify.com/v1/tracks/6J4LmfWAfxkEfQZE0OKCo6"
              }
             ]
           }
         }
         """
            .formatted(
                spotifyTrackItemJson.formatted(idJsonKeyValue), spotifyTrackItemJson.formatted(""));

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(getRecommendationsResponseJson)));

    // Then
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowGetRecommendationsExceptionWhenSpotifyReturnsEmptyResponseBody() {
    // Given
    String message = "getRecommendations.<return value>: must not be null";
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)));
    // Then
    Assertions.assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
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
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition().withBody(message).withStatus(401)));
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   {"error":{"status":403,"message":"Forbidden"}}
                   {"error":"invalid_client","error_description":"Invalid client secret"}
                   plain text
                   ""
                   """)
  void itShouldThrowForbiddenExceptionWhenSpotifyResponseHttpStatusCodeIs403(String message) {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition().withBody(message).withStatus(403)));
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(ForbiddenException.class)
        .hasMessage(FORBIDDEN + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   {"error":{"status":429,"message":"Too Many Requests"}}
                   {"error":"invalid_client","error_description":"Invalid client secret"}
                   plain text
                   ""
                   """)
  void itShouldThrowTooManyRequestExceptionWhenSpotifyResponseHttpStatusCodeIs429(String message) {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition().withBody(message).withStatus(429)));
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(TooManyRequestsException.class)
        .hasMessage(TOO_MANY_REQUESTS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
            400|{"error":{"status":400,"message":"Bad Request"}},
            500|{"error":{"status":500,"message":"Internal Server Error"}},
            502|{"error":{"status":502,"message":"Bad Gateway"}},
            503|{"error":{"status":503,"message":"Service Unavailable"}},
            400|{"error":"invalid_client","error_description":"Invalid client secret"}
            400|plain text
            400|""
            """)
  void itShouldThrowGetRecommendationsExceptionWhenSpotifyResponseHttpStatusCodeIsNot2XX(
      Integer status, String message) {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withBody(message)
                    .withStatus(status)));
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock = """
        {"id:"100",name":"something","size":"20"}
        Plain text""")
  void
      itShouldThrowGetRecommendationsExceptionWhenHttpResponseBodyNotAJsonRepresentationOfGetRecommendationsResponseClass(
          String responseBody) {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(
            UNABLE_TO_GET_RECOMMENDATIONS
                + "Error while extracting response for type [class "
                + GetRecommendationsResponse.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

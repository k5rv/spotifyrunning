package com.ksaraev.spotifyrun.service.toptracks;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTracksService;
import com.ksaraev.spotifyrun.service.UserTopTracksService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.*;

import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.ILLEGAL_TIME_RANGE;
import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class UserTopTracksServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyGetUserTopTracksRequestConfig requestConfig;
  @Mock private TrackMapper trackMapper;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> spotifyTrackItemsArgumentCaptor;
  private SpotifyUserTopTracksService underTest;
  private Validator validator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    underTest = new UserTopTracksService(spotifyClient, requestConfig, trackMapper);
  }

  @Test
  void itShouldGetTracks() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "Name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "Name";
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
                   "US",
                   "GB"
                 ],
                 "external_urls":{
                   "spotify":"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU"
                 },
                 "href":"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU",
                 "id":"0t58MAAaEcFOfp5aeljocU",
                 "images":[
                   {
                     "height":640,
                     "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                     "width":640
                   },
                   {
                     "height":300,
                     "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
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
                   "id":"%s",
                   "name":"%s",
                   "type":"artist",
                   "uri":"%s"
                 }
               ],
               "available_markets":[
                 "US",
                 "GB"
               ],
               "disc_number":1,
               "duration_ms":239493,
               "explicit":false,
               "external_ids":{
                 "isrc":"CCEA32135502"
               },
               "external_urls":{
                 "spotify":"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj"
               },
               "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj",
               "id":"%s",
               "is_local":false,
               "name":"%s",
               "popularity":%s,
               "preview_url":"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86",
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
    GetUserTopTracksResponse getUserTopTracksResponse =
        jsonToObject(getUserTopTracksResponseJson, GetUserTopTracksResponse.class);
    given(requestConfig.getLimit()).willReturn(1);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(getUserTopTracksResponse);
    given(trackMapper.mapItemsToTracks(anyList())).willReturn(List.of(track));
    // Then
    assertThat(underTest.getUserTopTracks()).containsExactly(track);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
                       |null
           UNKNOWN_TERM|UNKNOWN_TERM
           """)
  void itShouldThrowGetUserTopTracksExceptionWhenTimeRangeIsNotValid(
      String timeRange, String message) {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn(timeRange);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ILLEGAL_TIME_RANGE + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenTrackMapperThrowsRuntimeException() {
    // Given
    String message = "message";
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
                "id":"5Ko5Jn0OG8IDFEHhAYsCnj",
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
          }
          """;
    GetUserTopTracksResponse getUserTopTracksResponse =
        jsonToObject(getUserTopTracksResponseJson, GetUserTopTracksResponse.class);
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(getUserTopTracksResponse);
    given(trackMapper.mapItemsToTracks(anyList())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                  |Name|spotify:track:11bw2jzXRG1V25Zs0NdLWl|90 |FALSE|id: must not be null
           11bw2jzXRG1V25Zs0NdLWl|null|spotify:track:11bw2jzXRG1V25Zs0NdLWl|90 |FALSE|name: must not be empty
           11bw2jzXRG1V25Zs0NdLWl|Name|null                                |90 |FALSE|uri: must not be null
           11bw2jzXRG1V25Zs0NdLWl|Name|spotify:track:11bw2jzXRG1V25Zs0NdLWl|-1 |FALSE|popularity: must be greater than or equal to 0
           11bw2jzXRG1V25Zs0NdLWl|Name|spotify:track:11bw2jzXRG1V25Zs0NdLWl|101|FALSE|popularity: must be less than or equal to 100
           11bw2jzXRG1V25Zs0NdLWl|Name|spotify:track:11bw2jzXRG1V25Zs0NdLWl|90 |TRUE |artists: must not be empty
           """)
  void itShouldDetectTrackConstraintViolations(
      String id,
      String name,
      URI uri,
      Integer popularity,
      Boolean hasEmptyArtists,
      String message) {
    // Given
    List<SpotifyArtist> artists = Collections.singletonList(new Artist());
    if (hasEmptyArtists) {
      artists = Collections.emptyList();
    }
    SpotifyTrack track =
        Track.builder().id(id).name(name).uri(uri).popularity(popularity).artists(artists).build();
    // When
    Set<ConstraintViolation<SpotifyTrack>> violations = validator.validate(track);
    // Then
    assertThat(violations).hasSize(1);
    assertThat(new ConstraintViolationException(violations)).hasMessage(message);
  }

  @Test
  void itShouldDetectGetUserTopTracksResponseConstraintViolations() {
    // Given
    String message = "trackItems[1].id: must not be null";
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

    SpotifyTrackItem spotifyTrackItemOne =
        jsonToObject(spotifyTrackItemJson.formatted(idJsonKeyValue), SpotifyTrackItem.class);

    SpotifyTrackItem spotifyTrackItemTwo =
        jsonToObject(spotifyTrackItemJson.formatted(""), SpotifyTrackItem.class);

    GetUserTopTracksResponse getUserTopTracksResponse =
        new GetUserTopTracksResponse(
            null, Arrays.asList(spotifyTrackItemOne, spotifyTrackItemTwo), 2, 0, 2, null, null);
    // When
    Set<ConstraintViolation<GetUserTopTracksResponse>> violations =
        validator.validate(getUserTopTracksResponse);
    // Then
    assertThat(violations).hasSize(1);
    assertThat(new ConstraintViolationException(violations)).hasMessage(message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           FALSE|"type":"track"               |"name":"name"  |"popularity":32 |"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|id: must not be null
           FALSE|"id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"type":"track" |"popularity":32 |"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|name: must not be empty
           FALSE|"id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"  |"popularity":32 |"type":"track"                              |uri: must not be null
           FALSE|"id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"  |"popularity":-1 |"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|popularity: must be greater than or equal to 0
           FALSE|"id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"  |"popularity":101|"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|popularity: must be less than or equal to 100
           TRUE |"id":"5Ko5Jn0OG8IDFEHhAYsCnj"|"name":"name"  |"popularity":32 |"uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"|artistItems: must not be empty
           """)
  void itShouldDetectSpotifyTrackItemConstraintViolations(
      Boolean hasEmptyArtistsJsonKeyValue,
      String idJsonKeyValue,
      String nameJsonKeyValue,
      String popularityJsonKeyValue,
      String uriJsonKeyValue,
      String message) {

    String artistsJsonKeyValue =
        hasEmptyArtistsJsonKeyValue
            ? """
               "artists":[]"""
            : """
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
               ]
               """;

    String spotifyTrackItemJson =
        """
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
              "name":"name",
              "release_date":"2001-06-11",
              "release_date_precision":"day",
              "total_tracks":10,
              "type":"album",
              "uri":"spotify:album:0t58MAAaEcFOfp5aeljocU"
            },
            %s,
            "available_markets":[
              "US"
            ],
            "disc_number":1,
            "duration_ms":239493,
            "explicit":false,
            "external_ids":{
              "isrc":"CCEA32135502"
            },
            "external_urls":{
              "spotify":"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj"
            },
            "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj",
            %s,
            "is_local":false,
            %s,
            %s,
            "preview_url":"https://p.scdn.co/mp3-preview/abc",
            "track_number":9,
            %s
          }
          """
            .formatted(
                artistsJsonKeyValue,
                idJsonKeyValue,
                nameJsonKeyValue,
                popularityJsonKeyValue,
                uriJsonKeyValue);

    SpotifyTrackItem spotifyTrackItem = jsonToObject(spotifyTrackItemJson, SpotifyTrackItem.class);
    // When
    Set<ConstraintViolation<SpotifyTrackItem>> violations = validator.validate(spotifyTrackItem);
    // Then
    assertThat(violations).hasSize(1);
    assertThat(new ConstraintViolationException(violations)).hasMessage(message);
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListIsEmpty() {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(new GetUserTopTracksResponse(null, List.of(), 1, 0, 1, null, null));
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListIsNull() {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(new GetUserTopTracksResponse(null, null, 1, 0, 1, null, null));
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListElementsAreNull() {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    List<SpotifyTrackItem> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(null);
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(new GetUserTopTracksResponse(null, trackItems, 1, 0, 1, null, null));
    // When
    underTest.getUserTopTracks();
    // Then
    then(trackMapper).should().mapItemsToTracks(spotifyTrackItemsArgumentCaptor.capture());
    assertThat(spotifyTrackItemsArgumentCaptor.getValue()).isEmpty();
  }

  @Test
  void itShouldReturnNotNullElementsWhenSpotifyTrackItemsListContainsNullElements() {
    // Given
    String spotifyTrackItemJson =
        """
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
              "isrc":"CCEA32135502"
            },
            "external_urls":{
              "spotify":"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj"
            },
            "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj",
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj",
            "is_local":false,
            "name":"Name Name",
            "popularity":32,
            "preview_url":"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86",
            "track_number":9,
            "type":"track",
            "uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"
          }
          """;
    SpotifyTrackItem spotifyTrackItem = jsonToObject(spotifyTrackItemJson, SpotifyTrackItem.class);
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    List<SpotifyTrackItem> trackItems = new ArrayList<>();
    trackItems.add(spotifyTrackItem);
    trackItems.add(null);
    trackItems.add(null);
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(new GetUserTopTracksResponse(null, trackItems, 1, 0, 1, null, null));
    // When
    underTest.getUserTopTracks();
    // Then
    then(trackMapper).should().mapItemsToTracks(spotifyTrackItemsArgumentCaptor.capture());
    assertThat(spotifyTrackItemsArgumentCaptor.getAllValues())
        .containsExactly(Collections.singletonList(spotifyTrackItem));
  }
}

package com.ksaraev.spotifyrun.service.toptracks;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.GetUserTopTracksException;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static com.ksaraev.spotifyrun.exception.GetUserTopTracksException.*;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTopTracksServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private TrackMapper trackMapper;

  @Mock private SpotifyGetUserTopTracksRequestConfig requestConfig;
  private SpotifyUserTopTracksService underTest;
  private Validator validator;

  @BeforeAll
  public void setValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new UserTopTracksService(spotifyClient, requestConfig, trackMapper);
  }

  @Test
  void itShouldReturnUserTopTracks() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "Artist Name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    List<String> genres = List.of("post-punk");
    SpotifyArtist artist = new Artist(artistId, artistName, artistUri, genres);

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "Track Name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    int trackPopularity = 32;
    SpotifyTrack track = new Track(trackId, trackName, trackUri, trackPopularity, List.of(artist));

    String json =
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
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(jsonToObject(json, GetUserTopTracksResponse.class));
    given(trackMapper.mapItemsToTracks(anyList())).willReturn(List.of(track));
    // When and Then
    Assertions.assertThat(underTest.getUserTopTracks()).containsExactly(track);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String runtimeExceptionMessage = "Runtime exception message";
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any()))
        .willThrow(new RuntimeException(runtimeExceptionMessage));
    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ": " + runtimeExceptionMessage);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenTrackMapperThrowsRuntimeException() {
    // Given
    String json =
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
        }""";
    String runtimeExceptionMessage = "Runtime exception message";
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(jsonToObject(json, GetUserTopTracksResponse.class));
    given(trackMapper.mapItemsToTracks(anyList()))
        .willThrow(new RuntimeException(runtimeExceptionMessage));
    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ": " + runtimeExceptionMessage);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyClientReturnsNull() {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class))).willReturn(null);
    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ": " + SPOTIFY_CLIENT_RETURNED_NULL);
  }

  @Test
  void itShouldValidateWhenTrackIdIsNull() {
    // Given
    Track track = new Track();
    track.setId(null);
    // When
    Set<ConstraintViolation<Track>> violations = validator.validate(track);
    // Then
    assertThat(violations).hasSize(1);
    assertThat(new ConstraintViolationException(violations)).hasMessage("id: must not be null");
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenRequestTimeRangeParameterIsNull() {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    given(requestConfig.getTimeRange()).willReturn(null);
    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(
            UNABLE_TO_GET_USER_TOP_TRACKS
                + ": "
                + CONFIGURATION_ERROR_NOT_VALID_TIME_RANGE_PARAMETER.formatted("null"));
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenRequestTimeRangeParameterIsNotValid() {
    // Given
    given(requestConfig.getLimit()).willReturn(50);
    String value = "DUMMY";
    given(requestConfig.getTimeRange()).willReturn(value);
    // When and Then
    Assertions.assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isInstanceOf(GetUserTopTracksException.class)
        .hasMessage(
            UNABLE_TO_GET_USER_TOP_TRACKS
                + ": "
                + CONFIGURATION_ERROR_NOT_VALID_TIME_RANGE_PARAMETER.formatted(value));
  }
}

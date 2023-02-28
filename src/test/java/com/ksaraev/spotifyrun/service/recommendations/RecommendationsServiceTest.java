package com.ksaraev.spotifyrun.service.recommendations;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyForbiddenException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyTooManyRequestsException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetRecommendationsException;
import com.ksaraev.spotifyrun.exception.spotify.ForbiddenException;
import com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException;
import com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import com.ksaraev.spotifyrun.model.track.TrackFeaturesMapper;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.service.RecommendationsService;
import com.ksaraev.spotifyrun.service.SpotifyRecommendationsService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.*;

import static com.ksaraev.spotifyrun.exception.service.GetRecommendationsException.UNABLE_TO_GET_RECOMMENDATIONS;
import static com.ksaraev.spotifyrun.exception.spotify.ForbiddenException.FORBIDDEN;
import static com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException.TOO_MANY_REQUESTS;
import static com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException.UNAUTHORIZED;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class RecommendationsServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyGetRecommendationsRequestConfig requestConfig;
  @Mock private TrackMapper trackMapper;
  @Mock private TrackFeaturesMapper trackFeaturesMapper;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> spotifyTrackItemsArgumentCaptor;
  private SpotifyRecommendationsService underTest;
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    MockitoAnnotations.openMocks(this);
    underTest =
        new RecommendationsService(spotifyClient, requestConfig, trackMapper, trackFeaturesMapper);
  }

  @Test
  void itShouldGetTracks() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

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
                  "name":"Sleep Paralysis",
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

    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(1);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(jsonToObject(getRecommendationsResponseJson, GetRecommendationsResponse.class));
    given(trackMapper.mapItemsToTracks(anyList())).willReturn(List.of(track));

    // Then
    assertThat(underTest.getRecommendations(List.of(seedTrack), TrackFeatures.builder().build()))
        .containsExactly(track);
  }

  @Test
  void itShouldDetectGetRecommendationsResponseConstraintViolations() {
    // Given
    String message = "trackItems[1].id: must not be null";
    String spotifyTrackItemJson =
        """
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
              "name":"Sleep Paralysis",
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
                "id":"5VnrVRYzaatWXs102ScGwN",
                "name":"name",
                "type":"artist",
                "uri":"spotify:artist:5VnrVRYzaatWXs102ScGwN"
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
            "is_local":false,
            "name":"name",
            %s
            "popularity":55,
            "preview_url":"https://p.scdn.co/mp3-preview/cce282e15fecd2308ac7de6d3c1439bb8fffb304?cid=774b29d4f13844c495f206cafdad9c86",
            "track_number":1,
            "type":"track",
            "uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"
          }
          """;

    String idJsonKeyValue = """
            "id":"5Ko5Jn0OG8IDFEHhAYsCnj",""";

    SpotifyTrackItem spotifyTrackItemOne =
        jsonToObject(spotifyTrackItemJson.formatted(idJsonKeyValue), SpotifyTrackItem.class);

    SpotifyTrackItem spotifyTrackItemTwo =
        jsonToObject(spotifyTrackItemJson.formatted(""), SpotifyTrackItem.class);

    GetRecommendationsResponse getRecommendationsResponse =
        new GetRecommendationsResponse(
            Arrays.asList(spotifyTrackItemOne, spotifyTrackItemTwo), null);

    // When
    Set<ConstraintViolation<GetRecommendationsResponse>> violations =
        validator.validate(getRecommendationsResponse);
    // Then
    assertThat(violations).hasSize(1);
    assertThat(new ConstraintViolationException(violations)).hasMessage(message);
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListIsEmpty() {
    // Given
    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(50);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(new GetRecommendationsResponse(List.of(), null));
    // Then
    assertThat(underTest.getRecommendations(List.of(seedTrack), null)).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(spotifyTrackItemsArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListIsNull() {
    // Given
    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(50);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(new GetRecommendationsResponse(null, null));
    // Then
    assertThat(underTest.getRecommendations(List.of(seedTrack), null)).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(spotifyTrackItemsArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListElementsAreNull() {
    // Given
    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(50);

    List<SpotifyTrackItem> spotifyTrackItems = new ArrayList<>();
    spotifyTrackItems.add(null);
    spotifyTrackItems.add(null);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(new GetRecommendationsResponse(spotifyTrackItems, null));
    // Then
    assertThat(underTest.getRecommendations(List.of(seedTrack), null)).isEmpty();
    then(trackMapper).should().mapItemsToTracks(spotifyTrackItemsArgumentCaptor.capture());
    assertThat(spotifyTrackItemsArgumentCaptor.getValue()).isEmpty();
  }

  @Test
  void itShouldThrowUnauthorizedExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    String message = "message";

    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    List<SpotifyTrack> seedTracks = Collections.singletonList(seedTrack);

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(50);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(new SpotifyUnauthorizedException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @Test
  void itShouldThrowForbiddenExceptionWhenSpotifyClientThrowsSpotifyForbiddenException() {
    // Given
    String message = "message";

    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    List<SpotifyTrack> seedTracks = Collections.singletonList(seedTrack);

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(50);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(new SpotifyForbiddenException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage(FORBIDDEN + message);
  }

  @Test
  void itShouldThrowTooManyRequestExceptionWhenSpotifyClientThrowsSpotifyTooManyRequestException() {
    // Given
    String message = "message";

    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    List<SpotifyTrack> seedTracks = Collections.singletonList(seedTrack);

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(50);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(new SpotifyTooManyRequestsException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isInstanceOf(TooManyRequestsException.class)
        .hasMessage(TOO_MANY_REQUESTS + message);
  }

  @Test
  void itShouldThrowGetRecommendationsExceptionWhenTrackFeaturesMapperThrowsRuntimeException() {
    // Given
    String message = "message";

    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    List<SpotifyTrack> seedTracks = Collections.singletonList(seedTrack);

    given(trackFeaturesMapper.mapToRequestFeatures(any())).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowGetRecommendationsExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    List<SpotifyTrack> seedTracks = Collections.singletonList(seedTrack);

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowGetRecommendationsExceptionWhenTrackMapperThrowsRuntimeException() {
    // Given
    String message = "message";
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
                  "name":"Sleep Paralysis",
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
                    "id":"5VnrVRYzaatWXs102ScGwN",
                    "name":"name",
                    "type":"artist",
                    "uri":"spotify:artist:5VnrVRYzaatWXs102ScGwN"
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
                "id":"5Ko5Jn0OG8IDFEHhAYsCnj",
                "is_local":false,
                "name":"name",
                "popularity":55,
                "preview_url":"https://p.scdn.co/mp3-preview/cce282e15fecd2308ac7de6d3c1439bb8fffb304?cid=774b29d4f13844c495f206cafdad9c86",
                "track_number":1,
                "type":"track",
                "uri":"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"
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
          """;

    SpotifyTrack seedTrack =
        Track.builder()
            .id("0WNrVRYzaatWXs002ScGwN")
            .name("name")
            .uri(URI.create("spotify:track:0WNrVRYzaatWXs002ScGwN"))
            .popularity(55)
            .artists(Collections.emptyList())
            .build();

    List<SpotifyTrack> seedTracks = Collections.singletonList(seedTrack);

    String GetRecommendationsRequestTrackFeaturesJson = """
           {"minTempo":120}""";

    given(trackFeaturesMapper.mapToRequestFeatures(any()))
        .willReturn(
            jsonToObject(
                GetRecommendationsRequestTrackFeaturesJson,
                GetRecommendationsRequest.TrackFeatures.class));

    given(requestConfig.getLimit()).willReturn(1);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(jsonToObject(getRecommendationsResponseJson, GetRecommendationsResponse.class));
    given(trackMapper.mapItemsToTracks(anyList())).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, null))
        .isInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }
}

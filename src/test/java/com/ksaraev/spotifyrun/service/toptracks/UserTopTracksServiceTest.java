package com.ksaraev.spotifyrun.service.toptracks;

import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.ILLEGAL_TIME_RANGE;
import static com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.api.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetUserTopTracksException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTracksService;
import com.ksaraev.spotifyrun.service.UserTopTracksService;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserTopTracksServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyGetUserTopTracksRequestConfig requestConfig;
  @Mock private TrackMapper trackMapper;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> trackItemsArgumentCaptor;
  @Captor private ArgumentCaptor<GetUserTopTracksRequest> getUserTopTracksRequestArgumentCaptor;
  private SpotifyUserTopTracksService underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new UserTopTracksService(spotifyClient, requestConfig, trackMapper);
  }

  @Test
  void itShouldGetUserTopTracks() throws Exception {
    // Given
    String artistId = "1234567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:1234567890AaBbCcDdEeFfG");

    String trackId = "112233445AaBbCcDdEeFfG";
    String trackName = "track name";
    URI trackUri = URI.create("spotify:track:112233445AaBbCcDdEeFfG");
    Integer trackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> tracks = List.of(track);

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistId).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> trackItems = Collections.singletonList(trackItem);

    Integer offset = 0;
    Integer limit = 50;
    GetUserTopTracksRequest.TimeRange timeRange = GetUserTopTracksRequest.TimeRange.SHORT_TERM;

    GetUserTopTracksRequest getUserTopTracksRequest =
        GetUserTopTracksRequest.builder().offset(offset).limit(limit).timeRange(timeRange).build();

    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder()
            .href(
                new URL(
                    "https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term"))
            .trackItems(trackItems)
            .limit(limit)
            .offset(offset)
            .total(1)
            .next(null)
            .previous(null)
            .build();

    given(requestConfig.getLimit()).willReturn(limit);
    given(requestConfig.getOffset()).willReturn(offset);
    given(requestConfig.getTimeRange()).willReturn(timeRange.name());

    given(spotifyClient.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(getUserTopTracksResponse);

    given(trackMapper.mapItemsToTracks(anyList())).willReturn(tracks);

    // When
    underTest.getUserTopTracks();

    // Then
    then(spotifyClient).should().getUserTopTracks(getUserTopTracksRequestArgumentCaptor.capture());

    assertThat(getUserTopTracksRequestArgumentCaptor.getValue())
        .isNotNull()
        .isEqualTo(getUserTopTracksRequest);

    then(trackMapper).should().mapItemsToTracks(trackItemsArgumentCaptor.capture());

    assertThat(trackItemsArgumentCaptor.getAllValues()).isNotEmpty().containsExactly(trackItems);
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
  void itShouldThrowGetUserTopTracksExceptionWhenGetUserTopTracksRequestTimeRangeIsNotValid(
      String timeRange, String message) {
    // Given
    given(requestConfig.getTimeRange()).willReturn(timeRange);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ILLEGAL_TIME_RANGE + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    given(requestConfig.getTimeRange()).willReturn("MEDIUM_TERM");
    given(spotifyClient.getUserTopTracks(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenTrackMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyTrackItem trackItem = SpotifyTrackItem.builder().build();
    List<SpotifyTrackItem> trackItems = Collections.singletonList(trackItem);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackItems(trackItems).build();
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    given(trackMapper.mapItemsToTracks(trackItems)).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListIsEmpty() {
    // Given
    List<SpotifyTrackItem> trackItems = Collections.emptyList();
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackItems(trackItems).build();
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(trackItemsArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListElementsAreNull() {
    // Given
    List<SpotifyTrackItem> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackItems(trackItems).build();
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(trackItemsArgumentCaptor.capture());
  }

  @Test
  void
      itShouldReturnUserTopTracksNonNullElementsWhenGetUserTopTracksResponseTrackItemsListHasNullElements() {
    // Given
    SpotifyTrackItem trackItem = SpotifyTrackItem.builder().build();
    List<SpotifyTrackItem> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(trackItem);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackItems(trackItems).build();
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // When
    underTest.getUserTopTracks();
    // Then
    then(trackMapper).should().mapItemsToTracks(trackItemsArgumentCaptor.capture());
    assertThat(trackItemsArgumentCaptor.getAllValues())
        .containsExactly(Collections.singletonList(trackItem));
  }
}

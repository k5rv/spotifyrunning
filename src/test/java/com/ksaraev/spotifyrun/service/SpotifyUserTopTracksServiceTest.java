package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.GetUserTopTracksException.ILLEGAL_TIME_RANGE;
import static com.ksaraev.spotifyrun.exception.business.GetUserTopTracksException.UNABLE_TO_GET_USER_TOP_TRACKS;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.dto.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.client.dto.SpotifyTrackDto;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.exception.business.GetUserTopTracksException;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackMapper;
import com.ksaraev.spotifyrun.utils.SpotifyHelper;
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

class SpotifyUserTopTracksServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyGetUserTopTracksRequestConfig requestConfig;
  @Mock private SpotifyTrackMapper trackMapper;
  @Captor private ArgumentCaptor<List<SpotifyTrackDto>> trackItemsArgumentCaptor;
  @Captor private ArgumentCaptor<GetUserTopTracksRequest> getUserTopTracksRequestArgumentCaptor;
  private SpotifyUserTopTrackItemsService underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyUserTopTracksService(spotifyClient, requestConfig, trackMapper);
  }

  @Test
  void itShouldGetUserTopTracks() throws Exception {
    // Given
    List<SpotifyTrackItem> tracks = getTracks(2);
    List<SpotifyTrackDto> trackItems = getTrackItems(2);

    GetUserTopTracksRequest getUserTopTracksRequest = createGetUserTopTracksRequest();

    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyHelper.createGetUserTopTracksResponse(trackItems);

    given(requestConfig.getLimit()).willReturn(getUserTopTracksRequest.limit());
    given(requestConfig.getOffset()).willReturn(getUserTopTracksRequest.offset());
    given(requestConfig.getTimeRange()).willReturn(getUserTopTracksRequest.timeRange().name());

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
  void itShouldThrowGetUserTopTracksExceptionWhenTrackMapperThrowsRuntimeException()throws Exception {
    // Given
    String message = "message";
    List<SpotifyTrackDto> trackItems = getTrackItems(1);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
       createGetUserTopTracksResponse(trackItems);
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    given(trackMapper.mapItemsToTracks(trackItems)).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetUserTopTracksException.class)
        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListIsEmpty()throws Exception {
    // Given
    List<SpotifyTrackDto> trackItems = Collections.emptyList();
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        createGetUserTopTracksResponse(trackItems);
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(trackItemsArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListElementsAreNull()throws Exception {
    // Given
    List<SpotifyTrackDto> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        createGetUserTopTracksResponse(trackItems);
    given(requestConfig.getTimeRange()).willReturn(timeRangeName);
    given(spotifyClient.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(trackItemsArgumentCaptor.capture());
  }

  @Test
  void
      itShouldReturnUserTopTracksNonNullElementsWhenGetUserTopTracksResponseTrackItemsListHasNullElements() throws Exception{
    // Given
    SpotifyTrackDto trackItem = SpotifyTrackDto.builder().build();
    List<SpotifyTrackDto> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(trackItem);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        createGetUserTopTracksResponse(trackItems);
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

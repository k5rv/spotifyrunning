package com.suddenrun.spotify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.GetUserTopTracksRequest;
import com.suddenrun.spotify.client.dto.GetUserTopTracksResponse;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.config.GetSpotifyUserTopItemsRequestConfig;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
import com.suddenrun.utils.helpers.SpotifyClientHelper;
import com.suddenrun.utils.helpers.SpotifyServiceHelper;
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
  @Mock private SpotifyClient client;
  @Mock private GetSpotifyUserTopItemsRequestConfig config;
  @Mock private SpotifyTrackMapper mapper;
  @Captor private ArgumentCaptor<List<SpotifyTrackDto>> dtosArgumentCaptor;
  @Captor private ArgumentCaptor<GetUserTopTracksRequest> requestArgumentCaptor;
  private SpotifyUserTopTrackItemsService underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyUserTopTracksService(client, config, mapper);
  }

  @Test
  void itShouldGetUserTopTracks() throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<SpotifyTrackDto> trackDtos = SpotifyClientHelper.getTrackDtos(2);

    GetUserTopTracksRequest getUserTopTracksRequest =
        SpotifyClientHelper.createGetUserTopTracksRequest();

    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(trackDtos);

    given(config.getLimit()).willReturn(getUserTopTracksRequest.limit());
    given(config.getOffset()).willReturn(getUserTopTracksRequest.offset());
    given(config.getTimeRange()).willReturn(getUserTopTracksRequest.timeRange().name());

    given(client.getUserTopTracks(any(GetUserTopTracksRequest.class)))
        .willReturn(getUserTopTracksResponse);

    given(mapper.mapItemsToTracks(anyList())).willReturn(trackItems);

    // When
    underTest.getUserTopTracks();

    // Then
    then(client).should().getUserTopTracks(requestArgumentCaptor.capture());

    assertThat(requestArgumentCaptor.getValue()).isNotNull().isEqualTo(getUserTopTracksRequest);

    then(mapper).should().mapItemsToTracks(dtosArgumentCaptor.capture());

    assertThat(dtosArgumentCaptor.getAllValues()).isNotEmpty().containsExactly(trackDtos);
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
    given(config.getTimeRange()).willReturn(timeRange);
    // Then
    //    assertThatThrownBy(() -> underTest.getUserTopTracks())
    //        .isExactlyInstanceOf(GetUserTopTracksException.class)
    //        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + ILLEGAL_TIME_RANGE + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    given(config.getTimeRange()).willReturn("MEDIUM_TERM");
    given(client.getUserTopTracks(any())).willThrow(new RuntimeException(message));
    // Then
    //    assertThatThrownBy(() -> underTest.getUserTopTracks())
    //        .isExactlyInstanceOf(GetUserTopTracksException.class)
    //        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldThrowGetUserTopTracksExceptionWhenTrackMapperThrowsRuntimeException()
      throws Exception {
    // Given
    String message = "message";
    List<SpotifyTrackDto> trackDtos = SpotifyClientHelper.getTrackDtos(1);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    given(mapper.mapItemsToTracks(trackDtos)).willThrow(new RuntimeException(message));
    // Then
    //    assertThatThrownBy(() -> underTest.getUserTopTracks())
    //        .isExactlyInstanceOf(GetUserTopTracksException.class)
    //        .hasMessage(UNABLE_TO_GET_USER_TOP_TRACKS + message);
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListIsEmpty() throws Exception {
    // Given
    List<SpotifyTrackDto> trackItems = Collections.emptyList();
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(trackItems);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(mapper).should(never()).mapItemsToTracks(dtosArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListElementsAreNull()
      throws Exception {
    // Given
    List<SpotifyTrackDto> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(trackItems);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(mapper).should(never()).mapItemsToTracks(dtosArgumentCaptor.capture());
  }

  @Test
  void
      itShouldReturnUserTopTracksNonNullElementsWhenGetUserTopTracksResponseTrackItemsListHasNullElements()
          throws Exception {
    // Given
    SpotifyTrackDto trackItem = SpotifyTrackDto.builder().build();
    List<SpotifyTrackDto> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(trackItem);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(trackItems);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // When
    underTest.getUserTopTracks();
    // Then
    then(mapper).should().mapItemsToTracks(dtosArgumentCaptor.capture());
    assertThat(dtosArgumentCaptor.getAllValues())
        .containsExactly(Collections.singletonList(trackItem));
  }
}

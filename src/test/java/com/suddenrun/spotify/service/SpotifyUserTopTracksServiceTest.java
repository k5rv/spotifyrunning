package com.suddenrun.spotify.service;

import static com.suddenrun.utils.helpers.SpotifyClientHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.suddenrun.spotify.exception.GetSpotifyUserTopTracksException;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
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
  void itShouldGetUserTopTracks() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<SpotifyTrackDto> trackDtos = getTrackDtos(2);
    GetUserTopTracksRequest request = createGetUserTopTracksRequest();
    GetUserTopTracksResponse response = createGetUserTopTracksResponse(trackDtos);
    given(config.getLimit()).willReturn(request.limit());
    given(config.getOffset()).willReturn(request.offset());
    given(config.getTimeRange()).willReturn(request.timeRange().name());
    given(client.getUserTopTracks(any(GetUserTopTracksRequest.class))).willReturn(response);
    given(mapper.mapItemsToTracks(anyList())).willReturn(trackItems);
    // When
    underTest.getUserTopTracks();
    // Then
    then(client).should().getUserTopTracks(requestArgumentCaptor.capture());
    assertThat(requestArgumentCaptor.getValue()).isNotNull().isEqualTo(request);
    then(mapper).should().mapItemsToTracks(dtosArgumentCaptor.capture());
    assertThat(dtosArgumentCaptor.getAllValues()).isNotEmpty().containsExactly(trackDtos);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock = """
           null
           UNKNOWN_TERM
           """)
  void itShouldThrowGetSpotifyUserTopTracksExceptionWhenGetUserTopTracksRequestTimeRangeIsNotValid(
      String timeRange) {
    // Given
    given(config.getTimeRange()).willReturn(timeRange);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetSpotifyUserTopTracksException.class);
  }

  @Test
  void itShouldThrowGetSpotifyUserTopTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    given(config.getTimeRange()).willReturn("MEDIUM_TERM");
    given(client.getUserTopTracks(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetSpotifyUserTopTracksException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowGetSpotifyUserTopTracksExceptionWhenSpotifyTrackMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    List<SpotifyTrackDto> trackDtos = getTrackDtos(1);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    given(mapper.mapItemsToTracks(trackDtos)).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetSpotifyUserTopTracksException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackDtosListIsEmpty() {
    // Given
    List<SpotifyTrackDto> trackDtos = Collections.emptyList();
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(mapper).should(never()).mapItemsToTracks(dtosArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListElementsAreNull() {
    // Given
    List<SpotifyTrackDto> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackItems);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(mapper).should(never()).mapItemsToTracks(dtosArgumentCaptor.capture());
  }

  @Test
  void
      itShouldReturnUserTopTracksNonNullElementsWhenGetUserTopTracksResponseTrackDtoListHasNullElements() {
    // Given
    SpotifyTrackDto trackDto = SpotifyTrackDto.builder().build();
    List<SpotifyTrackDto> trackDtos = new ArrayList<>();
    trackDtos.add(null);
    trackDtos.add(trackDto);
    trackDtos.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // When
    underTest.getUserTopTracks();
    // Then
    then(mapper).should().mapItemsToTracks(dtosArgumentCaptor.capture());
    assertThat(dtosArgumentCaptor.getAllValues())
        .containsExactly(Collections.singletonList(trackDto));
  }
}

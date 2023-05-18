package com.suddenrun.spotify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.GetRecommendationsRequest;
import com.suddenrun.spotify.client.dto.GetRecommendationsResponse;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.config.GetSpotifyRecommendationItemsRequestConfig;
import com.suddenrun.spotify.model.SpotifyItem;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackFeatures;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackFeaturesMapper;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.suddenrun.utils.SpotifyHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyRecommendationsServiceTest {
  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  private static final String GET_RECOMMENDATIONS = "getRecommendations";
  @Mock private SpotifyClient spotifyClient;
  @Mock private GetSpotifyRecommendationItemsRequestConfig requestConfig;
  @Mock private SpotifyTrackMapper trackMapper;
  @Mock private SpotifyTrackFeaturesMapper trackFeaturesMapper;
  private SpotifyRecommendationsService underTest;
  @Captor private ArgumentCaptor<SpotifyTrackFeatures> trackFeaturesArgumentCaptor;
  @Captor private ArgumentCaptor<GetRecommendationsRequest> getRecommendationsRequestArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrackDto>> trackItemsArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest =
        new SpotifyRecommendationsService(
            spotifyClient, requestConfig, trackMapper, trackFeaturesMapper);
  }

  @Test
  void itShouldGetRecommendations() {
    // Given
    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    List<SpotifyTrackItem> recommendationTracks = SpotifyHelper.getTracks(10);
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> recommendationTrackItems = SpotifyHelper.getTrackItems(10);
    List<String> seedTrackIds = seedTracks.stream().map(SpotifyTrackItem::getId).toList();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsRequest getRecommendationsRequest =
        GetRecommendationsRequest.builder()
            .seedTrackIds(seedTrackIds)
            .trackFeatures(requestTrackFeatures)
            .limit(limit)
            .build();

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);

    given(requestConfig.getLimit()).willReturn(limit);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);

    given(trackMapper.mapItemsToTracks(anyList())).willReturn(recommendationTracks);

    // When
    underTest.getRecommendations(seedTracks, trackFeatures);

    // Then
    then(trackFeaturesMapper).should().mapToRequestFeatures(trackFeaturesArgumentCaptor.capture());

    assertThat(trackFeaturesArgumentCaptor.getValue()).isNotNull().isEqualTo(trackFeatures);

    then(spotifyClient)
        .should()
        .getRecommendations(getRecommendationsRequestArgumentCaptor.capture());

    assertThat(getRecommendationsRequestArgumentCaptor.getValue())
        .isNotNull()
        .isEqualTo(getRecommendationsRequest);

    then(trackMapper).should().mapItemsToTracks(trackItemsArgumentCaptor.capture());

    assertThat(trackItemsArgumentCaptor.getAllValues()).containsExactly(recommendationTrackItems);
  }

  @Test
  void itShouldGetRecommendationsWithoutFeaturesIncluded() {
    // Given
    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    List<SpotifyTrackItem> recommendationTracks = SpotifyHelper.getTracks(10);
    SpotifyTrackItemFeatures trackFeatures = SpotifyTrackFeatures.builder().build();
    List<SpotifyTrackDto> recommendationTrackItems = SpotifyHelper.getTrackItems(5);
    List<String> seedTrackIds = seedTracks.stream().map(SpotifyItem::getId).toList();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsRequest getRecommendationsRequest =
        GetRecommendationsRequest.builder()
            .seedTrackIds(seedTrackIds)
            .trackFeatures(requestTrackFeatures)
            .limit(limit)
            .build();

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);
    given(trackMapper.mapItemsToTracks(anyList())).willReturn(recommendationTracks);

    // When
    //underTest.getRecommendations(seedTracks);

    // Then
    then(trackFeaturesMapper).should().mapToRequestFeatures(trackFeaturesArgumentCaptor.capture());
    assertThat(trackFeaturesArgumentCaptor.getValue()).isNotNull().isEqualTo(trackFeatures);

    then(spotifyClient)
        .should()
        .getRecommendations(getRecommendationsRequestArgumentCaptor.capture());
    assertThat(getRecommendationsRequestArgumentCaptor.getValue())
        .isNotNull()
        .isEqualTo(getRecommendationsRequest);

    then(trackMapper).should().mapItemsToTracks(trackItemsArgumentCaptor.capture());
    assertThat(trackItemsArgumentCaptor.getAllValues()).containsExactly(recommendationTrackItems);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 6})
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTracksSizeIsNotValid(
      Integer tracksNumber) throws Exception {
    // Given

    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(tracksNumber);
    SpotifyTrackFeatures trackFeatures = SpotifyTrackFeatures.builder().build();

    Method getRecommendations =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTracks: size must be between 1 and 5");
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTracksContainsNullElements()
      throws Exception {
    // Given
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(1);
    tracks.add(null);

    SpotifyTrackFeatures trackFeatures = SpotifyTrackFeatures.builder().build();

    Method getRecommendations =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTracks[1].<list element>: must not be null");
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTracksIsNull() throws Exception {
    // Given
    SpotifyTrackFeatures trackFeatures = SpotifyTrackFeatures.builder().build();

    Method getRecommendations =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {null, trackFeatures};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTracks: must not be null");
  }

  @Test
  void itShouldDetectGetRecommendationsCascadeConstraintViolationsWhenSeedTracksElementIsNotValid()
      throws Exception {
    // Given
    String message = ".seedTracks[0].id: must not be null";

    SpotifyTrackItem track = SpotifyHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = List.of(track);

    SpotifyTrackFeatures trackFeatures = SpotifyTrackFeatures.builder().build();

    Method getRecommendations =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenTrackFeaturesIsNull()
      throws Exception {
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(2);

    Method getRecommendations =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {tracks, null};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".trackFeatures: must not be null");
  }

  @Test
  void itShouldThrowRecommendationExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";

    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(new RuntimeException(message));

    // Then
//    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
//        .isExactlyInstanceOf(GetRecommendationsException.class)
//        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowRecommendationExceptionWhenMapToRequestFeaturesThrowsRuntimeException() {
    // Given
    String message = "message";
    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willThrow(new RuntimeException(message));

    // Then
//    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
//        .isExactlyInstanceOf(GetRecommendationsException.class)
//        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowRecommendationExceptionWhenMapItemsToTracksThrowsRuntimeException() {
    // Given
    String message = "message";

    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> recommendationTrackItems = SpotifyHelper.getTrackItems(2);
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);

    given(requestConfig.getLimit()).willReturn(limit);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);

    given(trackMapper.mapItemsToTracks(anyList())).willThrow(new RuntimeException(message));

    // Then
//    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
//        .isExactlyInstanceOf(GetRecommendationsException.class)
//        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListIsEmpty() {
    // Given
    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> recommendationTrackItems = List.of();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;
    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);

    // Then
    assertThat(underTest.getRecommendations(seedTracks, trackFeatures)).isEmpty();
    then(trackMapper).should(never()).mapItemsToTracks(trackItemsArgumentCaptor.capture());

    assertThat(trackItemsArgumentCaptor.getAllValues()).isEmpty();
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListElementsAreNull() {
    // Given
    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> recommendationTrackItems = new ArrayList<>();
    recommendationTrackItems.add(null);
    recommendationTrackItems.add(null);
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);

    given(requestConfig.getLimit()).willReturn(limit);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);

    // Then
    assertThat(underTest.getRecommendations(seedTracks, trackFeatures)).isEmpty();

    then(trackMapper).should(never()).mapItemsToTracks(trackItemsArgumentCaptor.capture());

    assertThat(trackItemsArgumentCaptor.getAllValues()).isEmpty();
  }

  @Test
  void itShouldReturnNonNullElementsWhenSpotifyTrackItemsListContainsNullElements() {
    // Given
    List<SpotifyTrackItem> seedTracks = SpotifyHelper.getTracks(2);
    SpotifyTrackDto trackItem = SpotifyHelper.getTrackItem();
    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();

    List<SpotifyTrackDto> recommendationTrackItems = new ArrayList<>();
    recommendationTrackItems.add(null);
    recommendationTrackItems.add(trackItem);
    recommendationTrackItems.add(null);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);

    given(requestConfig.getLimit()).willReturn(limit);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);

    // When
    underTest.getRecommendations(seedTracks, trackFeatures);

    // Then
    then(trackMapper).should().mapItemsToTracks(trackItemsArgumentCaptor.capture());
    assertThat(trackItemsArgumentCaptor.getValue()).containsExactly(trackItem);
  }
}

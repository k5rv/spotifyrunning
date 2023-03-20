package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.GetRecommendationsException.UNABLE_TO_GET_RECOMMENDATIONS;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.api.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.exception.business.GetRecommendationsException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyItem;
import com.ksaraev.spotifyrun.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.trackfeatures.TrackFeatures;
import com.ksaraev.spotifyrun.model.trackfeatures.TrackFeaturesMapper;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
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

class RecommendationsServiceTest {
  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  private static final String GET_RECOMMENDATIONS = "getRecommendations";
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyGetRecommendationsRequestConfig requestConfig;
  @Mock private TrackMapper trackMapper;
  @Mock private TrackFeaturesMapper trackFeaturesMapper;
  private RecommendationsService underTest;
  @Captor private ArgumentCaptor<TrackFeatures> trackFeaturesArgumentCaptor;
  @Captor private ArgumentCaptor<GetRecommendationsRequest> getRecommendationsRequestArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> trackItemsArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest =
        new RecommendationsService(spotifyClient, requestConfig, trackMapper, trackFeaturesMapper);
  }

  @Test
  void itShouldGetRecommendations() {
    // Given
    List<SpotifyTrack> seedTracks = getTracks(2);
    List<SpotifyTrack> recommendationTracks = getTracks(10);
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();
    List<SpotifyTrackItem> recommendationTrackItems = getTrackItems(10);
    List<String> seedTrackIds = seedTracks.stream().map(SpotifyTrack::getId).toList();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsRequest getRecommendationsRequest =
        GetRecommendationsRequest.builder()
            .seedTrackIds(seedTrackIds)
            .trackFeatures(requestTrackFeatures)
            .limit(limit)
            .build();

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
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
    List<SpotifyTrack> seedTracks = getTracks(2);
    List<SpotifyTrack> recommendationTracks = getTracks(10);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().build();
    List<SpotifyTrackItem> recommendationTrackItems = getTrackItems(5);
    List<String> seedTrackIds = seedTracks.stream().map(SpotifyItem::getId).toList();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsRequest getRecommendationsRequest =
        GetRecommendationsRequest.builder()
            .seedTrackIds(seedTrackIds)
            .trackFeatures(requestTrackFeatures)
            .limit(limit)
            .build();

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);
    given(trackMapper.mapItemsToTracks(anyList())).willReturn(recommendationTracks);

    // When
    underTest.getRecommendations(seedTracks);

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

    List<SpotifyTrack> tracks = getTracks(tracksNumber);
    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<RecommendationsService>> constraintViolations =
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
    List<SpotifyTrack> tracks = getTracks(1);
    tracks.add(null);

    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<RecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTracks[1].<list element>: must not be null");
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTracksIsNull() throws Exception {
    // Given
    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackFeatures.class);

    Object[] parameterValues = {null, trackFeatures};

    // When
    Set<ConstraintViolation<RecommendationsService>> constraintViolations =
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

    SpotifyTrack track = getTrack();
    track.setId(null);
    List<SpotifyTrack> tracks = List.of(track);

    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<RecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenTrackFeaturesIsNull()
      throws Exception {
    List<SpotifyTrack> tracks = getTracks(2);

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackFeatures.class);

    Object[] parameterValues = {tracks, null};

    // When
    Set<ConstraintViolation<RecommendationsService>> constraintViolations =
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

    List<SpotifyTrack> seedTracks = getTracks(2);
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowRecommendationExceptionWhenMapToRequestFeaturesThrowsRuntimeException() {
    // Given
    String message = "message";
    List<SpotifyTrack> seedTracks = getTracks(2);
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldThrowRecommendationExceptionWhenMapItemsToTracksThrowsRuntimeException() {
    // Given
    String message = "message";

    List<SpotifyTrack> seedTracks = getTracks(2);
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();
    List<SpotifyTrackItem> recommendationTrackItems = getTrackItems(2);
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
        .willReturn(requestTrackFeatures);

    given(requestConfig.getLimit()).willReturn(limit);

    given(spotifyClient.getRecommendations(any(GetRecommendationsRequest.class)))
        .willReturn(getRecommendationsResponse);

    given(trackMapper.mapItemsToTracks(anyList())).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(seedTracks, trackFeatures))
        .isExactlyInstanceOf(GetRecommendationsException.class)
        .hasMessage(UNABLE_TO_GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackItemsListIsEmpty() {
    // Given
    List<SpotifyTrack> seedTracks = getTracks(2);
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();
    List<SpotifyTrackItem> recommendationTrackItems = List.of();
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;
    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
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
    List<SpotifyTrack> seedTracks = getTracks(2);
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();
    List<SpotifyTrackItem> recommendationTrackItems = new ArrayList<>();
    recommendationTrackItems.add(null);
    recommendationTrackItems.add(null);
    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
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
    List<SpotifyTrack> seedTracks = getTracks(2);
    SpotifyTrackItem trackItem = getTrackItem();
    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();

    List<SpotifyTrackItem> recommendationTrackItems = new ArrayList<>();
    recommendationTrackItems.add(null);
    recommendationTrackItems.add(trackItem);
    recommendationTrackItems.add(null);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(recommendationTrackItems).build();

    given(trackFeaturesMapper.mapToRequestFeatures(any(TrackFeatures.class)))
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

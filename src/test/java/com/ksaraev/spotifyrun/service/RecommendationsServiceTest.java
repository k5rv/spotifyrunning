package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.service.GetRecommendationsException.UNABLE_TO_GET_RECOMMENDATIONS;
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
import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrun.exception.service.GetRecommendationsException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import com.ksaraev.spotifyrun.model.track.TrackFeaturesMapper;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String seedTrackId = "0000567890AaBbCcDdEeFfG";
    String seedTrackName = "seed track name";
    URI seedTrackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
    Integer seedTrackPopularity = 51;

    String recommendationTrackId = "112233445AaBbCcDdEeFfG";
    String recommendationTrackName = "recommendation track name";
    URI recommendationTrackUri = URI.create("spotify:track:112233445AaBbCcDdEeFfG");
    Integer recommendationTrackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track seedTrack =
        Track.builder()
            .id(seedTrackId)
            .name(seedTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(seedTrack);

    Track recommendationTrack =
        Track.builder()
            .id(recommendationTrackId)
            .name(recommendationTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> recommendationTracks = List.of(recommendationTrack);

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistId).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(recommendationTrackId)
            .name(recommendationTrackName)
            .uri(recommendationTrackUri)
            .popularity(recommendationTrackPopularity)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> recommendationTrackItems = Collections.singletonList(trackItem);

    List<String> seedTrackIds = List.of(seedTrackId);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String seedTrackId = "0000567890AaBbCcDdEeFfG";
    String seedTrackName = "seed track name";
    URI seedTrackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
    Integer seedTrackPopularity = 51;

    String recommendationTrackId = "112233445AaBbCcDdEeFfG";
    String recommendationTrackName = "recommendation track name";
    URI recommendationTrackUri = URI.create("spotify:track:112233445AaBbCcDdEeFfG");
    Integer recommendationTrackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track seedTrack =
        Track.builder()
            .id(seedTrackId)
            .name(seedTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(seedTrack);

    Track recommendationTrack =
        Track.builder()
            .id(recommendationTrackId)
            .name(recommendationTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> recommendationTracks = List.of(recommendationTrack);

    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistId).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(recommendationTrackId)
            .name(recommendationTrackName)
            .uri(recommendationTrackUri)
            .popularity(recommendationTrackPopularity)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> recommendationTrackItems = Collections.singletonList(trackItem);

    List<String> seedTrackIds = List.of(seedTrackId);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().build();

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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String trackId = "0000567890AaBbCcDdEeFfG";
    String trackName = "seed track name";
    URI trackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
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

    List<SpotifyTrack> tracks = new ArrayList<>();

    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    IntStream.range(0, tracksNumber).forEach(index -> tracks.add(track));

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            "getRecommendations", List.class, SpotifyTrackFeatures.class);

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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String trackId = "0000567890AaBbCcDdEeFfG";
    String trackName = "seed track name";
    URI trackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
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

    List<SpotifyTrack> tracks = new ArrayList<>();
    tracks.add(track);
    tracks.add(null);

    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            "getRecommendations", List.class, SpotifyTrackFeatures.class);

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
            "getRecommendations", List.class, SpotifyTrackFeatures.class);

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

    SpotifyArtist artist =
        Artist.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:0000567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    SpotifyTrack track =
        Track.builder()
            .id(null)
            .name("seed track name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artists(artists)
            .build();

    List<SpotifyTrack> tracks = List.of(track);

    TrackFeatures trackFeatures = TrackFeatures.builder().build();

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            "getRecommendations", List.class, SpotifyTrackFeatures.class);

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

    Artist artist =
        Artist.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:0000567890AaBbCcDdEeFfG"))
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track track =
        Track.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("seed track name")
            .uri(URI.create("spotify:track:0000567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artists(artists)
            .build();

    List<SpotifyTrack> tracks = List.of(track);

    Method getRecommendations =
        RecommendationsService.class.getMethod(
            "getRecommendations", List.class, SpotifyTrackFeatures.class);

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

    Artist artist =
        Artist.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:0000567890AaBbCcDdEeFfG"))
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track track =
        Track.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("seed track name")
            .uri(URI.create("spotify:track:0000567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

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

    Artist artist =
        Artist.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:0000567890AaBbCcDdEeFfG"))
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track track =
        Track.builder()
            .id("0000567890AaBbCcDdEeFfG")
            .name("seed track name")
            .uri(URI.create("spotify:track:0000567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(track);

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

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

    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String seedTrackId = "0000567890AaBbCcDdEeFfG";
    String seedTrackName = "seed track name";
    URI seedTrackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
    Integer seedTrackPopularity = 51;

    String recommendationTrackId = "112233445AaBbCcDdEeFfG";
    String recommendationTrackName = "recommendation track name";
    URI recommendationTrackUri = URI.create("spotify:track:112233445AaBbCcDdEeFfG");
    Integer recommendationTrackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track seedTrack =
        Track.builder()
            .id(seedTrackId)
            .name(seedTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(seedTrack);

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistId).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(recommendationTrackId)
            .name(recommendationTrackName)
            .uri(recommendationTrackUri)
            .popularity(recommendationTrackPopularity)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> recommendationTrackItems = Collections.singletonList(trackItem);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String seedTrackId = "0000567890AaBbCcDdEeFfG";
    String seedTrackName = "seed track name";
    URI seedTrackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
    Integer seedTrackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track seedTrack =
        Track.builder()
            .id(seedTrackId)
            .name(seedTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(seedTrack);

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

    List<SpotifyTrackItem> recommendationTrackItems = List.of();

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String seedTrackId = "0000567890AaBbCcDdEeFfG";
    String seedTrackName = "seed track name";
    URI seedTrackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
    Integer seedTrackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track seedTrack =
        Track.builder()
            .id(seedTrackId)
            .name(seedTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(seedTrack);

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

    List<SpotifyTrackItem> recommendationTrackItems = new ArrayList<>();
    recommendationTrackItems.add(null);
    recommendationTrackItems.add(null);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

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
    String artistId = "0000567890AaBbCcDdEeFfG";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0000567890AaBbCcDdEeFfG");

    String seedTrackId = "0000567890AaBbCcDdEeFfG";
    String seedTrackName = "seed track name";
    URI seedTrackUri = URI.create("spotify:track:0000567890AaBbCcDdEeFfG");
    Integer seedTrackPopularity = 51;

    String recommendationTrackId = "112233445AaBbCcDdEeFfG";
    String recommendationTrackName = "recommendation track name";
    URI recommendationTrackUri = URI.create("spotify:track:112233445AaBbCcDdEeFfG");
    Integer recommendationTrackPopularity = 51;

    Artist artist =
        Artist.builder()
            .id(artistId)
            .name(artistName)
            .uri(artistUri)
            .genres(Collections.emptyList())
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    Track seedTrack =
        Track.builder()
            .id(seedTrackId)
            .name(seedTrackName)
            .uri(seedTrackUri)
            .popularity(seedTrackPopularity)
            .artists(artists)
            .build();

    List<SpotifyTrack> seedTracks = List.of(seedTrack);

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistId).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(recommendationTrackId)
            .name(recommendationTrackName)
            .uri(recommendationTrackUri)
            .popularity(recommendationTrackPopularity)
            .artistItems(artistItems)
            .build();

    BigDecimal minTempo = new BigDecimal(120);
    SpotifyTrackFeatures trackFeatures = TrackFeatures.builder().tempo(minTempo).build();

    List<SpotifyTrackItem> recommendationTrackItems = new ArrayList<>();
    recommendationTrackItems.add(null);
    recommendationTrackItems.add(trackItem);
    recommendationTrackItems.add(null);

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        GetRecommendationsRequest.TrackFeatures.builder().minTempo(minTempo).build();

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

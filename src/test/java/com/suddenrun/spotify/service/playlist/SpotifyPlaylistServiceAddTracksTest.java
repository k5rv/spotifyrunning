package com.suddenrun.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDetailsDto;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDto;
import com.suddenrun.spotify.client.dto.UpdatePlaylistItemsRequest;
import com.suddenrun.spotify.client.dto.UpdatePlaylistItemsResponse;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.suddenrun.spotify.exception.AddSpotifyPlaylistTracksExceptions;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.service.SpotifyPlaylistItemService;
import com.suddenrun.spotify.service.SpotifyPlaylistService;
import com.suddenrun.utils.helpers.SpotifyClientHelper;
import com.suddenrun.utils.helpers.SpotifyResourceHelper;
import com.suddenrun.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyPlaylistServiceAddTracksTest {

  private static final String ADD_TRACKS = "addTracks";
  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  @Mock private SpotifyClient client;
  @Mock private SpotifyPlaylistMapper mapper;

  @Mock private AddSpotifyPlaylistItemsRequestConfig requestConfig;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<UpdatePlaylistItemsRequest> requestArgumentCaptor;
  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @Test
  void itShouldAddTracks() {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<URI> uris = trackItems.stream().map(SpotifyTrackItem::getUri).toList();
    Integer position = 10;
    UpdatePlaylistItemsRequest request =
        UpdatePlaylistItemsRequest.builder().uris(uris).position(position).build();
    UpdatePlaylistItemsResponse response = SpotifyClientHelper.createUpdatePlaylistItemsResponse();
    given(requestConfig.getPosition()).willReturn(position);
    given(client.addPlaylistItems(any(), any())).willReturn(response);
    // When
    underTest.addTracks(playlistId, trackItems);
    // Then
    then(client)
        .should()
        .addPlaylistItems(playlistIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    given(client.addPlaylistItems(any(), any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlistId, trackItems))
        .isExactlyInstanceOf(AddSpotifyPlaylistTracksExceptions.class)
        .hasMessageContaining(playlistId)
        .hasMessageContaining(message);
  }

  @Test
  void
      addTracksShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    given(client.addPlaylistItems(any(), any())).willThrow(new SpotifyUnauthorizedException());
    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlistId, trackItems))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class);
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenSpotifyPlaylistIdIsNull()
      throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    Method method = SpotifyPlaylistService.class.getMethod(ADD_TRACKS, String.class, List.class);
    Object[] parameterValues = {null, trackItems};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".playlistId: must not be null");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListIsEmpty() throws Exception {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    List<SpotifyTrackItem> tracks = List.of();
    Object[] parameterValues = {playlistId, tracks};
    Method method = SpotifyPlaylistService.class.getMethod(ADD_TRACKS, String.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListSizeMoreThan100() throws Exception {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(101);
    Object[] parameterValues = {playlistId, tracks};
    Method method = SpotifyPlaylistService.class.getMethod(ADD_TRACKS, String.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenTrackListContainsNotValidElements()
      throws Exception {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(1);
    tracks.add(1, track);
    Object[] parameterValues = {playlistId, tracks};
    Method method = SpotifyPlaylistService.class.getMethod(ADD_TRACKS, String.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".trackItems[1].id: must not be null");
  }
}

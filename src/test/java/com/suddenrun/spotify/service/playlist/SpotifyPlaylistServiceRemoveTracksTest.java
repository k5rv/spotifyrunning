package com.suddenrun.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.RemovePlaylistItemsRequest;
import com.suddenrun.spotify.client.dto.RemovePlaylistItemsResponse;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.suddenrun.spotify.exception.RemoveSpotifyPlaylistTracksException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
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

class SpotifyPlaylistServiceRemoveTracksTest {

  private static final String REMOVE_TRACKS = "removeTracks";
  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  @Mock private SpotifyClient client;
  @Mock private SpotifyPlaylistMapper mapper;

  @Mock private AddSpotifyPlaylistItemsRequestConfig requestConfig;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<RemovePlaylistItemsRequest> requestArgumentCaptor;
  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @Test
  void itShouldRemoveTracks() {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<URI> uris = trackItems.stream().map(SpotifyTrackItem::getUri).toList();
    RemovePlaylistItemsRequest request =
        RemovePlaylistItemsRequest.builder().uris(uris).build();
    RemovePlaylistItemsResponse response = SpotifyClientHelper.createRemovePlaylistItemsResponse();
    given(client.removePlaylistItems(any(), any())).willReturn(response);
    // When
    underTest.removeTracks(playlistId, trackItems);
    // Then
    then(client)
        .should()
        .removePlaylistItems(playlistIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
  }

  @Test
  void itShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    given(client.removePlaylistItems(any(), any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.removeTracks(playlistId, trackItems))
        .isExactlyInstanceOf(RemoveSpotifyPlaylistTracksException.class)
        .hasMessageContaining(playlistId)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    given(client.removePlaylistItems(any(), any())).willThrow(new SpotifyUnauthorizedException());
    // Then
    assertThatThrownBy(() -> underTest.removeTracks(playlistId, trackItems))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class);
  }

  @Test
  void itShouldDetectRemoveTracksCascadeConstraintViolationWhenSpotifyPlaylistIdIsNull()
      throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    Method method = SpotifyPlaylistService.class.getMethod(REMOVE_TRACKS, String.class, List.class);
    Object[] parameterValues = {null, trackItems};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".playlistId: must not be null");
  }

  @Test
  void itShouldDetectRemoveTracksConstraintViolationWhenTrackListIsEmpty() throws Exception {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    List<SpotifyTrackItem> tracks = List.of();
    Object[] parameterValues = {playlistId, tracks};
    Method method = SpotifyPlaylistService.class.getMethod(REMOVE_TRACKS, String.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectRemoveTracksConstraintViolationWhenTrackListSizeMoreThan100() throws Exception {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(101);
    Object[] parameterValues = {playlistId, tracks};
    Method method = SpotifyPlaylistService.class.getMethod(REMOVE_TRACKS, String.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectRemoveTracksCascadeConstraintViolationWhenTrackListContainsNotValidElements()
      throws Exception {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(1);
    tracks.add(1, track);
    Object[] parameterValues = {playlistId, tracks};
    Method method = SpotifyPlaylistService.class.getMethod(REMOVE_TRACKS, String.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".trackItems[1].id: must not be null");
  }
}

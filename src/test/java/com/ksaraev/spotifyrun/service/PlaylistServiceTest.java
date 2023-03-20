package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.AddItemsRequest;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.exception.business.AddTracksException;
import com.ksaraev.spotifyrun.exception.business.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.business.GetPlaylistException;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlistdetails.PlaylistDetails;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;import jakarta.validation.ConstraintViolation;
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

class PlaylistServiceTest {

  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  private static final String ADD_TRACKS = "addTracks";
  private static final String CREATE_PLAYLIST = "createPlaylist";
  private static final String GET_PLAYLIST = "getPlaylist";

  private Method addTracksMethod;
  private Method getPlaylistMethod;
  private Method createPlaylistMethod;
  @Mock private SpotifyClient spotifyClient;
  @Mock private PlaylistMapper playlistMapper;
  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItemDetails> playlistItemDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItem> playlistItemArgumentCaptor;
  @Captor private ArgumentCaptor<PlaylistDetails> playlistDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<AddItemsRequest> addItemsRequestArgumentCaptor;
  private SpotifyPlaylistService underTest;

  @BeforeEach
  void setUp() throws Exception {
    getPlaylistMethod = PlaylistService.class.getMethod(GET_PLAYLIST, String.class);

    createPlaylistMethod =
        PlaylistService.class.getMethod(
            CREATE_PLAYLIST, SpotifyUser.class, SpotifyPlaylistDetails.class);

    addTracksMethod =
        PlaylistService.class.getMethod(ADD_TRACKS, SpotifyPlaylist.class, List.class);

    MockitoAnnotations.openMocks(this);
    underTest = new PlaylistService(spotifyClient, playlistMapper);
  }

  @Test
  void itShouldCreatePlaylist(){
    // Given
    SpotifyPlaylistItemDetails playlistItemDetails = getPlaylistItemDetails();
    SpotifyPlaylistItem playlistItem = getPlaylistItem();

    SpotifyUser user = getUser();
    PlaylistDetails playlistDetails = (PlaylistDetails) getPlaylistDetails();
    Playlist playlist = (Playlist) getPlaylist();

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class))).willReturn(playlist);

    // When
    underTest.createPlaylist(user, playlistDetails);

    // Then
    then(playlistMapper).should().mapToPlaylistItemDetails(playlistDetailsArgumentCaptor.capture());
    assertThat(playlistDetailsArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDetails);

    then(spotifyClient)
        .should()
        .createPlaylist(
            userIdArgumentCaptor.capture(), playlistItemDetailsArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(user.getId());
    assertThat(playlistItemDetailsArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistItemDetails);

    then(playlistMapper).should().mapToPlaylist(playlistItemArgumentCaptor.capture());
    assertThat(playlistItemArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistItem);
  }

  @Test
  void
      itShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistItemDetailsThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyUser spotifyUser = getUser();
    SpotifyPlaylistDetails spotifyPlaylistDetails = getPlaylistDetails();
    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistItem playlistItem = getPlaylistItem();
    SpotifyPlaylistItemDetails playlistItemDetails = getPlaylistItemDetails();
    SpotifyUser user = getUser();
    PlaylistDetails playlistDetails = (PlaylistDetails) getPlaylistDetails();
    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(user, playlistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldGetPlaylist(){
    // Given
    SpotifyPlaylistItem playlistItem = getPlaylistItem();
    Playlist playlist = (Playlist) getPlaylist();
    given(spotifyClient.getPlaylist(any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class))).willReturn(playlist);

    // When
    underTest.getPlaylist(playlist.getId());

    // Then
    then(spotifyClient).should().getPlaylist(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isNotNull().isEqualTo(playlist.getId());

    then(playlistMapper).should().mapToPlaylist(playlistItemArgumentCaptor.capture());
    assertThat(playlistItemArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistItem);
  }

  @Test
  void getPlaylistShouldThrowGetPlaylistExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    given(spotifyClient.getPlaylist(playlistId)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void
      getPlaylistShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistItem playlistItem = getPlaylistItem();
    String playlistId = playlistItem.id();

    given(spotifyClient.getPlaylist(any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void itShouldAddTracks() {
    // Given
    SpotifyPlaylist playlist = getPlaylist();
    List<SpotifyTrack> tracks = getTracks(2);
    List<URI> trackUris = tracks.stream().map(SpotifyTrack::getUri).toList();
    AddItemsRequest addItemsRequest = new AddItemsRequest(trackUris);

    // When
    underTest.addTracks(playlist, tracks);

    // Then
    then(spotifyClient)
        .should()
        .addItemsToPlaylist(
            playlistIdArgumentCaptor.capture(), addItemsRequestArgumentCaptor.capture());

    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlist.getId());

    assertThat(addItemsRequestArgumentCaptor.getValue()).isEqualTo(addItemsRequest);
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylist playlist = getPlaylist();
    List<SpotifyTrack> tracks = getTracks(2);
    given(spotifyClient.addItemsToPlaylist(any(), any())).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddTracksException.class)
        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenSpotifyPlaylistIsNotValid() {
    // Given
    SpotifyPlaylist playlist = getPlaylist();
    playlist.setId(null);
    List<SpotifyTrack> tracks = getTracks(2);
    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".playlist.id: must not be null");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenSpotifyPlaylistIsNull() {
    // Given
    List<SpotifyTrack> tracks = getTracks(2);
    Object[] parameterValues = {null, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".playlist: must not be null");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListIsEmpty() {
    // Given
    SpotifyPlaylist playlist = getPlaylist();
    List<SpotifyTrack> tracks = List.of();
    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".tracks: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListSizeMoreThan100() {
    // Given
    SpotifyPlaylist playlist = getPlaylist();
    List<SpotifyTrack> tracks = getTracks(101);
    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".tracks: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenTrackListContainsNotValidElements() {
    // Given
    SpotifyPlaylist playlist = getPlaylist();

    SpotifyTrack track = getTrack();
    track.setId(null);
    List<SpotifyTrack> tracks = getTracks(1);
    tracks.add(1, track);

    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".tracks[1].id: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistConstraintViolationsWhenSpotifyUserIsNull() {
    // Given
    SpotifyPlaylistDetails playlistDetails = getPlaylistDetails();
    Object[] parameterValues = {null, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, createPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".user: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistConstraintViolationsWhenSpotifyPlaylistDetailsIsNull() {
    // Given
    SpotifyUser user = getUser();

    Object[] parameterValues = {user, null};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, createPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".playlistDetails: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistCascadeConstraintViolationsWhenSpotifyUserIsNotValid() {
    // Given
    SpotifyUser user = getUser();
    user.setId(null);

    SpotifyPlaylistDetails playlistDetails = getPlaylistDetails();

    Object[] parameterValues = {user, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, createPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".user.id: must not be null");
  }

  @Test
  void
      itShouldDetectCreatePlaylistCascadeConstraintViolationsWhenSpotifyPlaylistDetailsIsNotValid() {
    // Given
    SpotifyUser user = getUser();

    SpotifyPlaylistDetails playlistDetails = getPlaylistDetails();
    playlistDetails.setName(null);

    Object[] parameterValues = {user, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, createPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".playlistDetails.name: must not be empty");
  }

  @Test
  void itShouldDetectGetPlaylistConstraintViolationsWhenPlaylistIdIsNull() {
    // Given
    Object[] parameterValues = {null};
    // When
    Set<ConstraintViolation<SpotifyPlaylistService>> constraintViolations =
        executableValidator.validateParameters(underTest, getPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_PLAYLIST + ".playlistId: must not be null");
  }
}

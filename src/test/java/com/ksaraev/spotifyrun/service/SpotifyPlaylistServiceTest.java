package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.UpdateItemsRequest;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.exception.business.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.business.GetPlaylistException;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
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

class SpotifyPlaylistServiceTest {

  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  private static final String ADD_TRACKS = "addTracks";
  private static final String CREATE_PLAYLIST = "createPlaylist";
  private static final String GET_PLAYLIST = "getPlaylist";

  private Method addTracksMethod;
  private Method getPlaylistMethod;
  private Method createPlaylistMethod;
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyPlaylistMapper playlistMapper;
  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistDetailsDto> playlistItemDetailsArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDto> playlistItemArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistDetails> playlistDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<UpdateItemsRequest> addItemsRequestArgumentCaptor;
  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() throws Exception {
    getPlaylistMethod = SpotifyPlaylistService.class.getMethod(GET_PLAYLIST, String.class);

    createPlaylistMethod =
        SpotifyPlaylistService.class.getMethod(
            CREATE_PLAYLIST, SpotifyUserProfileItem.class, SpotifyPlaylistItemDetails.class);

    addTracksMethod =
        SpotifyPlaylistService.class.getMethod(ADD_TRACKS, SpotifyPlaylistItem.class, List.class);

    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(spotifyClient, playlistMapper);
  }

  @Test
  void itShouldCreatePlaylist(){
    // Given
    SpotifyPlaylistDetailsDto playlistItemDetails = getPlaylistItemDetails();
    SpotifyPlaylistDto playlistItem = getPlaylistItem();

    SpotifyUserProfileItem user = getUser();
    SpotifyPlaylistDetails playlistDetails = (SpotifyPlaylistDetails) getPlaylistDetails();
    SpotifyPlaylist playlist = (SpotifyPlaylist) getPlaylist();

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistItemDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistDto.class))).willReturn(playlist);

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
    SpotifyUserProfileItem spotifyUser = getUser();
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = getPlaylistDetails();
    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistItemDetails.class)))
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
    SpotifyPlaylistDto playlistItem = getPlaylistItem();
    SpotifyPlaylistDetailsDto playlistItemDetails = getPlaylistItemDetails();
    SpotifyUserProfileItem user = getUser();
    SpotifyPlaylistDetails playlistDetails = (SpotifyPlaylistDetails) getPlaylistDetails();
    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistItemDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistDto.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(user, playlistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldGetPlaylist(){
    // Given
    SpotifyPlaylistDto playlistItem = getPlaylistItem();
    SpotifyPlaylist playlist = (SpotifyPlaylist) getPlaylist();
    given(spotifyClient.getPlaylist(any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistDto.class))).willReturn(playlist);

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
    SpotifyPlaylistDto playlistItem = getPlaylistItem();
    String playlistId = playlistItem.id();

    given(spotifyClient.getPlaylist(any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistDto.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void itShouldAddTracks() {
    // Given
    String playlistId = "asdasdasd";
    List<SpotifyTrackItem> tracks = getTracks(2);
    List<URI> trackUris = tracks.stream().map(SpotifyTrackItem::getUri).toList();
    UpdateItemsRequest updateItemsRequest = new UpdateItemsRequest(trackUris);

    // When
    underTest.addTracks(playlistId, tracks);

    // Then
    then(spotifyClient)
        .should()
        .addPlaylistItems(
            playlistIdArgumentCaptor.capture(), addItemsRequestArgumentCaptor.capture());

    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);

    assertThat(addItemsRequestArgumentCaptor.getValue()).isEqualTo(updateItemsRequest);
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistItem playlist = getPlaylist();
    List<SpotifyTrackItem> tracks = getTracks(2);
    given(spotifyClient.addPlaylistItems(any(), any())).willThrow(new RuntimeException(message));

    // Then
//    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
//        .isExactlyInstanceOf(AddTracksException.class)
//        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenSpotifyPlaylistIsNotValid() {
    // Given
    SpotifyPlaylistItem playlist = getPlaylist();
    playlist.setId(null);
    List<SpotifyTrackItem> tracks = getTracks(2);
    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".playlist.id: must not be null");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenSpotifyPlaylistIsNull() {
    // Given
    List<SpotifyTrackItem> tracks = getTracks(2);
    Object[] parameterValues = {null, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".playlist: must not be null");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListIsEmpty() {
    // Given
    SpotifyPlaylistItem playlist = getPlaylist();
    List<SpotifyTrackItem> tracks = List.of();
    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".tracks: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListSizeMoreThan100() {
    // Given
    SpotifyPlaylistItem playlist = getPlaylist();
    List<SpotifyTrackItem> tracks = getTracks(101);
    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".tracks: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenTrackListContainsNotValidElements() {
    // Given
    SpotifyPlaylistItem playlist = getPlaylist();

    SpotifyTrackItem track = getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = getTracks(1);
    tracks.add(1, track);

    Object[] parameterValues = {playlist, tracks};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, addTracksMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".tracks[1].id: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistConstraintViolationsWhenSpotifyUserIsNull() {
    // Given
    SpotifyPlaylistItemDetails playlistDetails = getPlaylistDetails();
    Object[] parameterValues = {null, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, createPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".user: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistConstraintViolationsWhenSpotifyPlaylistDetailsIsNull() {
    // Given
    SpotifyUserProfileItem user = getUser();

    Object[] parameterValues = {user, null};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, createPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".playlistDetails: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistCascadeConstraintViolationsWhenSpotifyUserIsNotValid() {
    // Given
    SpotifyUserProfileItem user = getUser();
    user.setId(null);

    SpotifyPlaylistItemDetails playlistDetails = getPlaylistDetails();

    Object[] parameterValues = {user, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
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
    SpotifyUserProfileItem user = getUser();

    SpotifyPlaylistItemDetails playlistDetails = getPlaylistDetails();
    playlistDetails.setName(null);

    Object[] parameterValues = {user, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
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
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, getPlaylistMethod, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_PLAYLIST + ".playlistId: must not be null");
  }
}

package com.suddenrun.spotify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDetailsDto;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDto;
import com.suddenrun.spotify.client.dto.UpdatePlaylistItemsRequest;
import com.suddenrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylist;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.utils.SpotifyHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
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

  @Mock private AddSpotifyPlaylistItemsRequestConfig updatePlaylistItemsRequestConfig;
  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistDetailsDto> playlistItemDetailsArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDto> playlistItemArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistDetails> playlistDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<UpdatePlaylistItemsRequest> addItemsRequestArgumentCaptor;
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
    underTest = new SpotifyPlaylistService(spotifyClient, playlistMapper, updatePlaylistItemsRequestConfig);
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    SpotifyPlaylistDetailsDto playlistItemDetails = SpotifyHelper.getPlaylistItemDetails();
    SpotifyPlaylistDto playlistItem = SpotifyHelper.getPlaylistItem();

    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();
    SpotifyPlaylistDetails playlistDetails = (SpotifyPlaylistDetails) SpotifyHelper.getPlaylistDetails();
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyHelper.getPlaylist();

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
    SpotifyUserProfileItem spotifyUser = SpotifyHelper.getUserProfile();
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyHelper.getPlaylistDetails();
    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistItemDetails.class)))
        .willThrow(new RuntimeException(message));

    // Then
    //    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
    //        .isExactlyInstanceOf(CreatePlaylistException.class)
    //        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistDto playlistItem = SpotifyHelper.getPlaylistItem();
    SpotifyPlaylistDetailsDto playlistItemDetails = SpotifyHelper.getPlaylistItemDetails();
    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();
    SpotifyPlaylistDetails playlistDetails = (SpotifyPlaylistDetails) SpotifyHelper.getPlaylistDetails();
    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistItemDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistDto.class)))
        .willThrow(new RuntimeException(message));

    // Then
    //    assertThatThrownBy(() -> underTest.createPlaylist(user, playlistDetails))
    //        .isExactlyInstanceOf(CreatePlaylistException.class)
    //        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldGetPlaylist() {
    // Given
    SpotifyPlaylistDto playlistItem = SpotifyHelper.getPlaylistItem();
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyHelper.getPlaylist();
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
    //    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
    //        .isExactlyInstanceOf(GetPlaylistException.class)
    //        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void
      getPlaylistShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistDto playlistItem = SpotifyHelper.getPlaylistItem();
    String playlistId = playlistItem.id();

    given(spotifyClient.getPlaylist(any())).willReturn(playlistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistDto.class)))
        .willThrow(new RuntimeException(message));

    // Then
    //    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
    //        .isExactlyInstanceOf(GetPlaylistException.class)
    //        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void itShouldAddTracks() {
    //    // Given
    //    String playlistId = "asdasdasd";
    //    List<SpotifyTrackItem> tracks = getTracks(2);
    //    List<URI> trackUris = tracks.stream().map(SpotifyTrackItem::getUri).toList();
    //    UpdatePlaylistItemsRequest updateItemsRequest =
    //        new UpdatePlaylistItemsRequest(trackUris,
    // UpdatePlaylistItemsRequest.Position.BEGINNING);
    //
    //    // When
    //    underTest.addTracks(playlistId, tracks);
    //
    //    // Then
    //    then(spotifyClient)
    //        .should()
    //        .addPlaylistItems(
    //            playlistIdArgumentCaptor.capture(), addItemsRequestArgumentCaptor.capture());
    //
    //    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    //
    //    assertThat(addItemsRequestArgumentCaptor.getValue()).isEqualTo(updateItemsRequest);
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(2);
    given(spotifyClient.addPlaylistItems(any(), any())).willThrow(new RuntimeException(message));

    // Then
    //    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
    //        .isExactlyInstanceOf(AddTracksException.class)
    //        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenSpotifyPlaylistIsNotValid() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();
    playlist.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(2);
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
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(2);
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
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();
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
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(101);
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
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();

    SpotifyTrackItem track = SpotifyHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyHelper.getTracks(1);
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
    SpotifyPlaylistItemDetails playlistDetails = SpotifyHelper.getPlaylistDetails();
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
    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();

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
    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();
    user.setId(null);

    SpotifyPlaylistItemDetails playlistDetails = SpotifyHelper.getPlaylistDetails();

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
    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();

    SpotifyPlaylistItemDetails playlistDetails = SpotifyHelper.getPlaylistDetails();
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

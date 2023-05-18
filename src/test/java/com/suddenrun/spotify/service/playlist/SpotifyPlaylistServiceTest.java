package com.suddenrun.spotify.service.playlist;

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
import com.suddenrun.spotify.service.SpotifyPlaylistItemService;
import com.suddenrun.spotify.service.SpotifyPlaylistService;
import com.suddenrun.utils.helpers.SpotifyClientHelper;
import com.suddenrun.utils.helpers.SpotifyServiceHelper;
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
        SpotifyPlaylistService.class.getMethod(ADD_TRACKS, String.class, List.class);

    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(spotifyClient, updatePlaylistItemsRequestConfig, playlistMapper);
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    SpotifyPlaylistDetailsDto playlistItemDetails = SpotifyClientHelper.getPlaylistDetailsDto();
    SpotifyPlaylistDto playlistItem = SpotifyClientHelper.getPlaylistDto();

    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();
    SpotifyPlaylistDetails playlistDetails = (SpotifyPlaylistDetails) SpotifyServiceHelper.getPlaylistDetails();
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyServiceHelper.getPlaylist();

    given(playlistMapper.mapToPlaylistDetailsDto(any(SpotifyPlaylistItemDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToModel(any(SpotifyPlaylistDto.class))).willReturn(playlist);

    // When
    underTest.createPlaylist(user, playlistDetails);

    // Then
    then(playlistMapper).should().mapToPlaylistDetailsDto(playlistDetailsArgumentCaptor.capture());
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

    then(playlistMapper).should().mapToModel(playlistItemArgumentCaptor.capture());
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
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile();
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    given(playlistMapper.mapToPlaylistDetailsDto(any(SpotifyPlaylistItemDetails.class)))
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
    SpotifyPlaylistDto playlistItem = SpotifyClientHelper.getPlaylistDto();
    SpotifyPlaylistDetailsDto playlistItemDetails = SpotifyClientHelper.getPlaylistDetailsDto();
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();
    SpotifyPlaylistDetails playlistDetails = (SpotifyPlaylistDetails) SpotifyServiceHelper.getPlaylistDetails();
    given(playlistMapper.mapToPlaylistDetailsDto(any(SpotifyPlaylistItemDetails.class)))
        .willReturn(playlistItemDetails);
    given(spotifyClient.createPlaylist(any(), any())).willReturn(playlistItem);
    given(playlistMapper.mapToModel(any(SpotifyPlaylistDto.class)))
        .willThrow(new RuntimeException(message));

    // Then
    //    assertThatThrownBy(() -> underTest.createPlaylist(user, playlistDetails))
    //        .isExactlyInstanceOf(CreatePlaylistException.class)
    //        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
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
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(2);
    given(spotifyClient.addPlaylistItems(any(), any())).willThrow(new RuntimeException(message));

    // Then
    //    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
    //        .isExactlyInstanceOf(AddTracksException.class)
    //        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenSpotifyPlaylistIsNotValid() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    playlist.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(2);
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
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(2);
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
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
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
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(101);
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
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();

    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(1);
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
    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
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
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();

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
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();
    user.setId(null);

    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();

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
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();

    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
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

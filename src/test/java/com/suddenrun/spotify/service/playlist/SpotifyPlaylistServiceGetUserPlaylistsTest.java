package com.suddenrun.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.*;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.suddenrun.spotify.exception.GetSpotifyUserPlaylistsException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyPlaylistServiceGetUserPlaylistsTest {
  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

  @Mock private SpotifyClient client;

  @Mock private AddSpotifyPlaylistItemsRequestConfig requestConfig;

  @Mock private SpotifyPlaylistMapper mapper;

  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;

  @Captor private ArgumentCaptor<GetUserPlaylistsRequest> requestArgumentCaptor;

  @Captor private ArgumentCaptor<List<SpotifyPlaylistDto>> playlistDtosArgumentCaptor;

  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @Test
  void itShouldGetUserPlaylists() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = SpotifyClientHelper.getPlaylistDtos(2);
    List<SpotifyPlaylistItem> playlists = SpotifyServiceHelper.getPlaylists(2);
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    given(mapper.mapDtosToModels(any())).willReturn(playlists);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).should().mapDtosToModels(playlistDtosArgumentCaptor.capture());
    assertThat(playlistDtosArgumentCaptor.getValue()).isNotNull().isEqualTo(playlistDtos);
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyPlaylistDtosIsEmpty() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = List.of();
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).shouldHaveNoInteractions();
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyPlaylistDtosAreNull() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = new ArrayList<>();
    playlistDtos.add(null);
    playlistDtos.add(null);
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).shouldHaveNoInteractions();
  }

  @Test
  void itShouldReturnNonNullElementsWhenSpotifyPlaylistDtosContainsNulls() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = new ArrayList<>();
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto();
    playlistDtos.add(null);
    playlistDtos.add(playlistDto);
    playlistDtos.add(null);
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).should().mapDtosToModels(playlistDtosArgumentCaptor.capture());
    assertThat(playlistDtosArgumentCaptor.getValue()).isNotNull().containsExactly(playlistDto);
  }

  @Test
  void
      itShouldThrowGetSpotifyUserPlaylistsExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    given(client.getPlaylists(any(), any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserPlaylists(userProfile))
        .isExactlyInstanceOf(GetSpotifyUserPlaylistsException.class)
        .hasMessageContaining(userId)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    String message = "message";
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    given(client.getPlaylists(any(), any())).willThrow(new SpotifyUnauthorizedException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserPlaylists(userProfile))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowGetSpotifyUserPlaylistsExceptionWhenPlaylistMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = SpotifyClientHelper.getPlaylistDtos(2);
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    given(mapper.mapDtosToModels(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getUserPlaylists(userProfile))
        .isExactlyInstanceOf(GetSpotifyUserPlaylistsException.class)
        .hasMessageContaining(userId)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldDetectGetUserPlaylistsConstraintViolationsWhenSpotifyUserItemIsNull()
      throws Exception {
    // Given
    Method method =
        SpotifyPlaylistService.class.getMethod("getUserPlaylists", SpotifyUserProfileItem.class);
    Object[] parameterValues = {null};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("getUserPlaylists.userProfileItem: must not be null");
  }

  @Test
  void itShouldDetectGetUserPlaylistsConstraintViolationsWhenSpotifyUserItemIsNotValid()
      throws Exception {
    // Given
    SpotifyUserProfileItem userProfileItem = SpotifyServiceHelper.getUserProfile();
    userProfileItem.setId(null);
    Method method =
        SpotifyPlaylistService.class.getMethod("getUserPlaylists", SpotifyUserProfileItem.class);
    Object[] parameterValues = {userProfileItem};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("getUserPlaylists.userProfileItem.id: must not be null");
  }
}

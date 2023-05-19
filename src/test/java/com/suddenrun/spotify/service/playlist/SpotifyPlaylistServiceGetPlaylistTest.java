package com.suddenrun.spotify.service.playlist;

import com.suddenrun.spotify.client.SpotifyClient;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDto;
import com.suddenrun.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.suddenrun.spotify.config.AddSpotifyPlaylistItemsRequestConfig;
import com.suddenrun.spotify.exception.GetSpotifyPlaylistException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylist;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
import com.suddenrun.spotify.service.SpotifyPlaylistItemService;
import com.suddenrun.spotify.service.SpotifyPlaylistService;
import com.suddenrun.utils.helpers.SpotifyClientHelper;
import com.suddenrun.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class SpotifyPlaylistServiceGetPlaylistTest {
  private static final ExecutableValidator executableValidator =
      Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

  @Mock private SpotifyClient client;

  @Mock private AddSpotifyPlaylistItemsRequestConfig requestConfig;

  @Mock private SpotifyPlaylistMapper mapper;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDto> playlistDtoArgumentCaptor;

  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @Test
  void itShouldGetPlaylist() {
    // Given
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto();
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyServiceHelper.getPlaylist();
    given(client.getPlaylist(any())).willReturn(playlistDto);
    given(mapper.mapToModel(any(SpotifyPlaylistDto.class))).willReturn(playlist);
    // When
    underTest.getPlaylist(playlist.getId());
    // Then
    then(client).should().getPlaylist(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isNotNull().isEqualTo(playlist.getId());
    then(mapper).should().mapToModel(playlistDtoArgumentCaptor.capture());
    assertThat(playlistDtoArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDto);
  }

  @Test
  void itShouldThrowGetSpotifyPlaylistExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    given(client.getPlaylist(playlistId)).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetSpotifyPlaylistException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    String message = "message";
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    given(client.getPlaylist(playlistId)).willThrow(new SpotifyUnauthorizedException(message));
    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowGetSpotifyPlaylistExceptionWhenPlaylistMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto(playlistId);
    given(client.getPlaylist(any())).willReturn(playlistDto);
    given(mapper.mapToModel(any(SpotifyPlaylistDto.class)))
        .willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetSpotifyPlaylistException.class)
        .hasMessageContaining(playlistId)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldDetectGetPlaylistConstraintViolationsWhenPlaylistIdIsNull() throws Exception {
    // Given
    Method method = SpotifyPlaylistService.class.getMethod("getPlaylist", String.class);
    Object[] parameterValues = {null};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("getPlaylist.playlistId: must not be null");
  }
}

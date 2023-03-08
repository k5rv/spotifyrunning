package com.ksaraev.spotifyrun.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.*;
import com.ksaraev.spotifyrun.utils.SpotifyClientStub;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreatePlaylistTest {

  private final String CREATE_PLAYLIST = "createPlaylist";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method =
        SpotifyClient.class.getMethod(
            CREATE_PLAYLIST, String.class, SpotifyPlaylistItemDetails.class);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectCreatePlaylistMethodConstraintViolationWhenUserIdIsNull() {
    // Given
    String message = ".userId: must not be null";
    // When
    SpotifyPlaylistItemDetails playlistItemDetails =
        SpotifyPlaylistItemDetails.builder().name("name").build();
    Object[] parameterValues = {null, playlistItemDetails};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldDetectCreatePlaylistMethodConstraintViolationWhenSpotifyPlaylistItemDetailsIsNull() {
    // Given
    String message = ".playlistItemDetails: must not be null";
    String userId = "12122604372";
    // When
    Object[] parameterValues = {userId, null};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + message);
  }

  @Test
  void
      itShouldDetectCreatePlaylistMethodCascadeConstraintViolationWhenSpotifyPlaylistItemDetailsIsNotValid() {
    // Given
    String message = ".playlistItemDetails.name: must not be empty";
    String userId = "12122604372";
    SpotifyPlaylistItemDetails playlistItemDetails =
        SpotifyPlaylistItemDetails.builder().name(null).build();
    // When
    Object[] parameterValues = {userId, playlistItemDetails};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + message);
  }

  @Test
  void
      itShouldDetectCreatePlaylistMethodCascadeConstraintViolationWhenReturnValueSpotifyItemPlaylistIsNotValid() {
    // Given
    String message = ".<return value>.id: must not be null";

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id("12122604372")
            .displayName("name")
            .email("email@gmail.com")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    SpotifyPlaylistItem playlistItem =
        SpotifyPlaylistItem.builder()
            .id(null)
            .name("playlist name")
            .uri(URI.create("spotify:playlist:0moWPCTPTShumonjlsDgLe"))
            .userProfileItem(userProfileItem)
            .snapshotId("MywyM2Y2Zjg5YTdlNGQ3MmI2OGFiN2NiZmQ4NTNlZDdlMjE2OTFjODM4")
            .build();
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, playlistItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldDetectCreatePlaylistMethodConstraintViolationWhenReturnValueIsNull() {
    // Given
    String message = ".<return value>: must not be null";
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + message);
  }
}

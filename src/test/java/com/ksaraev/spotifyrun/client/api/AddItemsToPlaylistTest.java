package com.ksaraev.spotifyrun.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.utils.SpotifyClientStub;
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

class AddItemsToPlaylistTest {

  private final String ADD_ITEMS_TO_PLAYLIST = "addItemsToPlaylist";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method =
        SpotifyClient.class.getMethod(ADD_ITEMS_TO_PLAYLIST, String.class, AddItemsRequest.class);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectAddItemsToPlaylistMethodConstraintViolationWhenPlaylistIdIsNull() {
    // Given
    String message = ".playlistId: must not be null";
    URI trackUri = URI.create("spotify:track:1234567890AaBbCcDdEeFfG");
    List<URI> uris = List.of(trackUri);
    AddItemsRequest addItemsRequest = AddItemsRequest.builder().itemUris(uris).build();
    // When
    Object[] parameterValues = {null, addItemsRequest};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_ITEMS_TO_PLAYLIST + message);
  }

  @Test
  void itShouldDetectAddItemsToPlaylistMethodConstraintViolationWhenAddItemsRequestIsNull() {
    // Given
    String message = ".request: must not be null";
    String playlistId = "0moWPCTPTShumonjlsDgLe";
    // When
    Object[] parameterValues = {playlistId, null};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_ITEMS_TO_PLAYLIST + message);
  }

  @Test
  void
      itShouldDetectAddItemsToPlaylistMethodCascadeConstraintViolationWhenAddItemsToPlaylistRequestIsNotValid() {
    // Given
    String message = ".request.itemUris: size must be between 1 and 100";
    String playlistId = "0moWPCTPTShumonjlsDgLe";
    List<URI> uris = List.of();
    AddItemsRequest addItemsRequest = AddItemsRequest.builder().itemUris(uris).build();
    // When
    Object[] parameterValues = {playlistId, addItemsRequest};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_ITEMS_TO_PLAYLIST + message);
  }

  @Test
  void
      itShouldDetectAddItemsToPlaylistMethodCascadeConstraintViolationWhenReturnValueAddItemsToPlaylistResponseIsNotValid() {
    // Given
    String message = ".<return value>.snapshotId: must not be empty";
    AddItemsResponse addItemsResponse = AddItemsResponse.builder().snapshotId(null).build();
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, addItemsResponse);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_ITEMS_TO_PLAYLIST + message);
  }

  @Test
  void itShouldDetectAddItemsToPlaylistMethodConstraintViolationWhenReturnValueIsNull() {
    // Given
    String message = ".<return value>: must not be null";
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_ITEMS_TO_PLAYLIST + message);
  }
}

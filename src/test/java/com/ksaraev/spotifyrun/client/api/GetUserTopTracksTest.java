package com.ksaraev.spotifyrun.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
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

class GetUserTopTracksTest {

  private final String GET_USER_TOP_TRACKS = "getUserTopTracks";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method = SpotifyClient.class.getMethod(GET_USER_TOP_TRACKS, GetUserTopTracksRequest.class);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectGetUserTopTracksMethodConstraintViolationWhenGetUserTopTracksRequestIsNull() {
    // Given
    String message = ".request: must not be null";
    // When
    Object[] parameterValues = {null};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + message);
  }

  @Test
  void
      itShouldDetectGetUserTopTracksMethodCascadeConstraintViolationWhenGetUserTopTracksRequestIsNotValid() {
    // Given
    GetUserTopTracksRequest getUserTopTracksRequest =
        GetUserTopTracksRequest.builder().limit(51).offset(0).build();
    // When
    Object[] parameterValues = {getUserTopTracksRequest};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + ".request.limit: must be less than or equal to 50");
  }

  @Test
  void itShouldDetectGetUserTopTracksMethodConstraintViolationWhenReturnValueIsNull() {
    // Given
    String message = ".<return value>: must not be null";
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + message);
  }

  @Test
  void
      itShouldDetectGetUserTopTracksMethodCascadeConstraintViolationWhenReturnValueGetUserTopTracksResponseIsNotValid() {
    // Given
    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(null)
            .name("track name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> trackItems = List.of(trackItem);

    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackItems(trackItems).build();

    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, getUserTopTracksResponse);

    // Then
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + ".<return value>.trackItems[0].id: must not be null");
  }
}

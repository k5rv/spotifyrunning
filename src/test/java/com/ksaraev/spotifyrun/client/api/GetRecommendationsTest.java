package com.ksaraev.spotifyrun.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.items.SpotifyArtistDto;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackDto;
import com.ksaraev.spotifyrun.utils.SpotifyClientStub;
import com.ksaraev.spotifyrun.utils.SpotifyHelper;
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

class GetRecommendationsTest {

  private final String GET_RECOMMENDATIONS = "getRecommendations";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method = SpotifyClient.class.getMethod(GET_RECOMMENDATIONS, GetRecommendationsRequest.class);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void
      itShouldDetectGetRecommendationsMethodConstraintViolationWhenGetRecommendationsRequestIsNull() {
    // Given
    String message = ".request: must not be null";
    // When
    Object[] parameterValues = {null};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldDetectGetRecommendationsMethodConstraintViolationWhenReturnValueIsNull() {
    // Given
    String message = ".<return value>: must not be null";
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + message);
  }

  @Test
  void
      itShouldDetectGetRecommendationsMethodCascadeConstraintViolationWhenReturnValueGetRecommendationsResponseIsNotValid() {
    // Given
    List<SpotifyArtistDto> artistItems = SpotifyHelper.getArtistItems(1);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id(null)
            .name("track name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackDto> trackItems = List.of(trackItem);

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(trackItems).build();

    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, getRecommendationsResponse);

    // Then
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".<return value>.trackItems[0].id: must not be null");
  }
}

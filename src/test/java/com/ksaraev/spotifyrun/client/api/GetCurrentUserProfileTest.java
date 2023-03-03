package com.ksaraev.spotifyrun.client.api;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.utils.SpotifyClientDummy;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GetCurrentUserProfileTest {

  private final String GET_CURRENT_USER_PROFILE = "getCurrentUserProfile";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientDummy();
    method = SpotifyClient.class.getMethod(GET_CURRENT_USER_PROFILE);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectGetCurrentUserProfileMethodConstraintViolationWhenReturnValueIsNull() {
    String message = ".<return value>: must not be null";
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_CURRENT_USER_PROFILE + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null       |Konstantin|email@gmail.com|spotify:user:12122604372|.<return value>.id: must not be null
           12122604372|null      |email@gmail.com|spotify:user:12122604372|.<return value>.displayName: must not be empty
           12122604372|Konstantin|email@         |spotify:user:12122604372|.<return value>.email: must be a well-formed email address
           12122604372|Konstantin|email@gmail.com|null                    |.<return value>.uri: must not be null
           """)
  void
      itShouldDetectGetCurrentUserProfileMethodConstraintViolationWhenSpotifyUserProfileItemReturnValueIsNotValid(
          String id, String displayName, String email, URI uri, String message) {
    // Given
    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(id)
            .displayName(displayName)
            .email(email)
            .uri(uri)
            .build();
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, userProfileItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_CURRENT_USER_PROFILE + message);
  }
}

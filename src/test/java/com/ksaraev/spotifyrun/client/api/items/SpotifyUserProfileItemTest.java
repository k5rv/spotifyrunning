package com.ksaraev.spotifyrun.client.api.items;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyUserProfileItemTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null       |Konstantin|email@gmail.com|spotify:user:12122604372|id: must not be null
           12122604372|null      |email@gmail.com|spotify:user:12122604372|displayName: must not be empty
           12122604372|Konstantin|email@         |spotify:user:12122604372|email: must be a well-formed email address
           12122604372|Konstantin|email@gmail.com|null                    |uri: must not be null
           """)
  void itShouldDetectSpotifyUserProfileItemConstraintViolations(
      String id, String name, String email, URI uri, String message) {
    // Given
    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder().id(id).displayName(name).uri(uri).email(email).build();
    // When
    Set<ConstraintViolation<SpotifyUserProfileItem>> constraintViolations =
        validator.validate(userProfileItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

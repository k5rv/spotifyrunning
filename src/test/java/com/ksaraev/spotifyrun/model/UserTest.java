package com.ksaraev.spotifyrun.model;

import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
                   null       |Konstantin|email@gmail.com|spotify:user:12122604372|id: must not be null
                   12122604372|null      |email@gmail.com|spotify:user:12122604372|name: must not be empty
                   12122604372|Konstantin|email@         |spotify:user:12122604372|email: must be a well-formed email address
                   12122604372|Konstantin|email@gmail.com|null                    |uri: must not be null
                   """)
  void itShouldDetectUserConstraintViolations(
      String id, String name, String email, URI uri, String message) {
    // Given
    User user = User.builder().id(id).name(name).email(email).uri(uri).build();
    // When
    Set<ConstraintViolation<SpotifyUser>> constraintViolations = validator.validate(user);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

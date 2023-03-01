package com.ksaraev.spotifyrun.client.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GetUserTopTracksRequestTest {

  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
              51|0 |limit: must be less than or equal to 50
              0 |0 |limit: must be greater than or equal to 1
              2 |-1|offset: must be greater than or equal to 0
              """)
  void itShouldDetectGetUserTopTracksRequestConstraintViolations(
      Integer limit, Integer offset, String message) {
    // Given
    GetUserTopTracksRequest getUserTopTracksRequest =
        GetUserTopTracksRequest.builder().limit(limit).offset(offset).build();

    // When
    Set<ConstraintViolation<GetUserTopTracksRequest>> constraintViolations =
        validator.validate(getUserTopTracksRequest);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

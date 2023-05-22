package com.ksaraev.spotify.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePlaylistItemsResponseTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void itShouldDetectAddItemsResponseConstraintViolations() {
    // Given
    UpdatePlaylistItemsResponse updatePlaylistItemsResponse =
        UpdatePlaylistItemsResponse.builder().snapshotId(null).build();
    // When
    Set<ConstraintViolation<UpdatePlaylistItemsResponse>> constraintViolations =
        validator.validate(updatePlaylistItemsResponse);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("snapshotId: must not be empty");
  }
}

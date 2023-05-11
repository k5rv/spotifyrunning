package com.suddenrun.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateUpdateItemsResponseTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void itShouldDetectAddItemsResponseConstraintViolations() {
    // Given
    UpdateUpdateItemsResponse updateUpdateItemsResponse =
        UpdateUpdateItemsResponse.builder().snapshotId(null).build();
    // When
    Set<ConstraintViolation<UpdateUpdateItemsResponse>> constraintViolations =
        validator.validate(updateUpdateItemsResponse);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("snapshotId: must not be empty");
  }
}

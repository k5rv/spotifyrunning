package com.ksaraev.spotifyrun.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddItemsRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListIsEmpty() {
    // Given
    List<URI> uris = List.of();
    AddItemsRequest addItemsRequest = AddItemsRequest.builder().itemUris(uris).build();
    // When
    Set<ConstraintViolation<AddItemsRequest>> constraintViolations =
        validator.validate(addItemsRequest);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("itemUris: must not be empty");
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListHasNullElements() {
    // Given
    URI uriA = URI.create("spotify:resource:a");
    URI uriB = null;
    URI uriC = URI.create("spotify:resource:c");
    List<URI> uris = new ArrayList<>();
    uris.add(uriA);
    uris.add(uriB);
    uris.add(uriC);
    AddItemsRequest addItemsRequest = AddItemsRequest.builder().itemUris(uris).build();
    // When
    Set<ConstraintViolation<AddItemsRequest>> constraintViolations =
        validator.validate(addItemsRequest);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("itemUris[1].<list element>: must not be null");
  }
}

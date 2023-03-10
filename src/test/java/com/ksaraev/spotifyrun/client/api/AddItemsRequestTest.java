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
import java.util.stream.IntStream;
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
        .hasMessage("itemUris: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListHasMoreThan100Elements() {
    // Given
    URI uri = URI.create("spotify:resource:a");
    List<URI> uris = new ArrayList<>();
    IntStream.rangeClosed(0,100).forEach(index -> uris.add(index, uri));
    AddItemsRequest addItemsRequest = AddItemsRequest.builder().itemUris(uris).build();
    // When
    Set<ConstraintViolation<AddItemsRequest>> constraintViolations =
            validator.validate(addItemsRequest);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
            .hasMessage("itemUris: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListHasNullElements() {
    // Given
    URI uriA = URI.create("spotify:resource:a");
    URI uriC = URI.create("spotify:resource:c");
    List<URI> uris = new ArrayList<>();
    uris.add(uriA);
    uris.add(null);
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

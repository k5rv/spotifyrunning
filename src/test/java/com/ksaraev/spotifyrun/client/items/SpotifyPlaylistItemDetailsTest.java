package com.ksaraev.spotifyrun.client.items;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpotifyPlaylistItemDetailsTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemDetailsConstraintViolations() {
    // Given
    SpotifyPlaylistItemDetails playlistItemDetails =
        SpotifyPlaylistItemDetails.builder().name(null).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemDetails>> constraintViolations =
        validator.validate(playlistItemDetails);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("name: must not be empty");
  }
}

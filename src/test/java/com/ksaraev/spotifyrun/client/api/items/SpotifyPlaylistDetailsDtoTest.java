package com.ksaraev.spotifyrun.client.api.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDetailsDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpotifyPlaylistDetailsDtoTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemDetailsConstraintViolations() {
    // Given
    SpotifyPlaylistDetailsDto playlistItemDetails =
        SpotifyPlaylistDetailsDto.builder().name(null).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistDetailsDto>> constraintViolations =
        validator.validate(playlistItemDetails);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("name: must not be empty");
  }
}

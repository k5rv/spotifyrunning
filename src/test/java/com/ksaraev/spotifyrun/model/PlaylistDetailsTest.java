package com.ksaraev.spotifyrun.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlaylistDetailsTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void itShouldDetectSpotifyPlaylistConstraintViolations() {
    // Given
    String message = "name: must not be empty";
    SpotifyPlaylistDetails playlistDetails = PlaylistDetails.builder().name(null).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistDetails>> constraintViolations =
        validator.validate(playlistDetails);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

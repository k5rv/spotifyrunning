package com.ksaraev.spotifyrun.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArtistTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                  |artist name|spotify:artist:012345012345AABBccDDee|id: must not be null
           012345012345AABBccDDee|null       |spotify:artist:012345012345AABBccDDee|name: must not be empty
           012345012345AABBccDDee|artist name|null                                 |uri: must not be null
           """)
  void itShouldDetectSpotifyArtistConstraintViolations(
      String id, String name, URI uri, String message) {
    // Given
    SpotifyArtist artist = Artist.builder().id(id).name(name).uri(uri).build();
    // When
    Set<ConstraintViolation<SpotifyArtist>> constraintViolations = validator.validate(artist);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

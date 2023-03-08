package com.ksaraev.spotifyrun.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrackTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                   |track name|spotify:track:1234567890AaBbCcDdEeFfG|51 |TRUE |id: must not be null
           1234567890AaBbCcDdEeFfG|null      |spotify:track:1234567890AaBbCcDdEeFfG|51 |TRUE |name: must not be empty
           1234567890AaBbCcDdEeFfG|track name|null                                 |51 |TRUE |uri: must not be null
           1234567890AaBbCcDdEeFfG|track name|spotify:track:1234567890AaBbCcDdEeFfG|-1 |TRUE |popularity: must be greater than or equal to 0
           1234567890AaBbCcDdEeFfG|track name|spotify:track:1234567890AaBbCcDdEeFfG|101|TRUE |popularity: must be less than or equal to 100
           1234567890AaBbCcDdEeFfG|track name|spotify:track:1234567890AaBbCcDdEeFfG|51 |FALSE|artists: must not be empty
           """)
  void itShouldDetectTrackConstraintViolations(
      String id, String name, URI uri, Integer popularity, Boolean hasArtists, String message) {
    // Given
    SpotifyArtist artist =
        Artist.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtist> artists = hasArtists ? List.of(artist) : List.of();

    SpotifyTrack track =
        Track.builder().id(id).name(name).uri(uri).popularity(popularity).artists(artists).build();

    // When
    Set<ConstraintViolation<SpotifyTrack>> constraintViolations = validator.validate(track);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyTrackCascadeConstraintViolations() {
    // Given
    String message = "artists[0].id: must not be null";

    SpotifyArtist artist =
        Artist.builder()
            .id(null)
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtist> artists = List.of(artist);

    SpotifyTrack track =
        Track.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("track name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artists(artists)
            .build();

    // When
    Set<ConstraintViolation<SpotifyTrack>> constraintViolations = validator.validate(track);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

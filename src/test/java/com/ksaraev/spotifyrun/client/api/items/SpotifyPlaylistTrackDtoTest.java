package com.ksaraev.spotifyrun.client.api.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.api.SpotifyArtistDto;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistTrackDto;
import com.ksaraev.spotifyrun.client.api.SpotifyTrackDto;
import com.ksaraev.spotifyrun.client.api.SpotifyUserProfileDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistTrackDtoTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           TRUE |FALSE|FALSE|trackItem: must not be null
           FALSE|TRUE |FALSE|addedBy: must not be null
           FALSE|FALSE|TRUE |addedAt: must not be null
           """)
  void itShouldDetectSpotifyPlaylistItemTrackConstraintViolations(
      Boolean isTrackItemNull, Boolean isAddedByNull, Boolean isAddedAtNull, String message) {
    // Given

    SpotifyUserProfileDto userProfileItem =
        isAddedByNull
            ? null
            : SpotifyUserProfileDto.builder()
                .id("12122604372")
                .displayName("name")
                .uri(URI.create("spotify:user:12122604372"))
                .email("email@mail.com")
                .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItem =
        isTrackItemNull
            ? null
            : SpotifyTrackDto.builder()
                .id("1234567890AaBbCcDdEeFfG")
                .name("playlist name")
                .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
                .popularity(51)
                .artistItems(artistItems)
                .build();

    String addedAt = isAddedAtNull ? null : "2020-12-04T14:14:36Z";

    SpotifyPlaylistTrackDto playlistItemTrack =
        SpotifyPlaylistTrackDto.builder()
            .trackItem(trackItem)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistTrackDto>> constraintViolations =
        validator.validate(playlistItemTrack);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemTrackCascadeConstraintViolations() {
    // Given
    String message = "addedBy.id: must not be null";

    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id(null)
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("playlist name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistItems(artistItems)
            .build();

    SpotifyPlaylistTrackDto playlistItemTrack =
        SpotifyPlaylistTrackDto.builder()
            .trackItem(trackItem)
            .addedBy(userProfileItem)
            .addedAt("2020-12-04T14:14:36Z")
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistTrackDto>> constraintViolations =
        validator.validate(playlistItemTrack);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

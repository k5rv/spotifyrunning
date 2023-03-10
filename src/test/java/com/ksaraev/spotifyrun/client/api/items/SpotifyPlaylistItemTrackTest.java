package com.ksaraev.spotifyrun.client.api.items;

import static org.assertj.core.api.Assertions.assertThat;

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

class SpotifyPlaylistItemTrackTest {

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

    SpotifyUserProfileItem userProfileItem =
        isAddedByNull
            ? null
            : SpotifyUserProfileItem.builder()
                .id("12122604372")
                .displayName("name")
                .uri(URI.create("spotify:user:12122604372"))
                .email("email@mail.com")
                .build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        isTrackItemNull
            ? null
            : SpotifyTrackItem.builder()
                .id("1234567890AaBbCcDdEeFfG")
                .name("playlist name")
                .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
                .popularity(51)
                .artistItems(artistItems)
                .build();

    String addedAt = isAddedAtNull ? null : "2020-12-04T14:14:36Z";

    SpotifyPlaylistItemTrack playlistItemTrack =
        SpotifyPlaylistItemTrack.builder()
            .trackItem(trackItem)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemTrack>> constraintViolations =
        validator.validate(playlistItemTrack);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemTrackCascadeConstraintViolations() {
    // Given
    String message = "addedBy.id: must not be null";

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(null)
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("playlist name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistItems(artistItems)
            .build();

    SpotifyPlaylistItemTrack playlistItemTrack =
        SpotifyPlaylistItemTrack.builder()
            .trackItem(trackItem)
            .addedBy(userProfileItem)
            .addedAt("2020-12-04T14:14:36Z")
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemTrack>> constraintViolations =
        validator.validate(playlistItemTrack);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

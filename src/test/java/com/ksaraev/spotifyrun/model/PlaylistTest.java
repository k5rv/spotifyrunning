package com.ksaraev.spotifyrun.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlaylistTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                  |playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |id: must not be null
           0moWPCTPTShumonjlsDgLe|null         |spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |name: must not be empty
           0moWPCTPTShumonjlsDgLe|playlist name|null                                   |MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |uri: must not be null
           0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|null                                                    |TRUE |snapshotId: must not be null
           0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|FALSE|owner: must not be null
           """)
  void itShouldDetectSpotifyPlaylistConstraintViolations(
      String id, String name, URI uri, String snapshotId, Boolean hasOwner, String message) {
    // Given
    SpotifyUser user = null;

    if (hasOwner) {
      user =
          User.builder()
              .id("12122604372")
              .name("name")
              .uri(URI.create("spotify:user:12122604372"))
              .email("email@mail.com")
              .build();
    }

    SpotifyPlaylist playlist =
        Playlist.builder().id(id).name(name).uri(uri).snapshotId(snapshotId).owner(user).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylist>> constraintViolations = validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistCascadeConstraintViolations() {
    // Given
    SpotifyUser user =
        User.builder()
            .id(null)
            .name("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyPlaylist playlist =
        Playlist.builder()
            .id("0moWPCTPTShumonjlsDgLe")
            .name("playlist name")
            .uri(URI.create("spotify:playlist:0moWPCTPTShumonjlsDgLe"))
            .snapshotId("MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1")
            .owner(user)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylist>> constraintViolations = validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("owner.id: must not be null");
  }
}

package com.ksaraev.spotifyrun.model;

import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.user.AppUser;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
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
      user = getUser();
    }

    SpotifyPlaylist playlist =
        Playlist.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .snapshotId(snapshotId)
            .owner((AppUser) user)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylist>> constraintViolations = validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistCascadeConstraintViolations() {
    // Given
    SpotifyUser user = getUser();
    user.setId(null);

    SpotifyPlaylist playlist = getPlaylist();
    playlist.setOwner(user);

    // When
    Set<ConstraintViolation<SpotifyPlaylist>> constraintViolations = validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("owner.id: must not be null");
  }
}

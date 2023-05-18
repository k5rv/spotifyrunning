package com.suddenrun.spotify.model.playlist;

import static com.suddenrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistItemTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                  |playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |id: must not be null
           0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|null                                                    |TRUE |snapshotId: must not be null
           0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|FALSE|owner: must not be null
           """)
  void itShouldDetectSpotifyPlaylistConstraintViolations(
      String id, String name, URI uri, String snapshotId, Boolean hasOwner, String message) {
    // Given
    SpotifyUserProfileItem user = null;

    if (hasOwner) user = getUserProfile();

    SpotifyPlaylistItem playlist =
        SpotifyPlaylist.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .snapshotId(snapshotId)
            .owner(user)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistItem>> constraintViolations =
        validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistCascadeConstraintViolations() {
    // Given
    SpotifyUserProfileItem user = getUserProfile();
    user.setId(null);

    SpotifyPlaylistItem playlist = getPlaylist();
    playlist.setOwner(user);

    // When
    Set<ConstraintViolation<SpotifyPlaylistItem>> constraintViolations =
        validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("owner.id: must not be null");
  }
}

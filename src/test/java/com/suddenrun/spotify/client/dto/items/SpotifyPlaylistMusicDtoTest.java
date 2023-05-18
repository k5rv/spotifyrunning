package com.suddenrun.spotify.client.dto.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.suddenrun.spotify.client.dto.SpotifyPlaylistMusicDto;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistTrackDto;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.client.dto.SpotifyUserProfileDto;
import com.suddenrun.utils.SpotifyHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistMusicDtoTest {

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
            : SpotifyHelper.getUserProfileItem();

    SpotifyTrackDto trackItem =
        isTrackItemNull
            ? null
            : SpotifyHelper.getTrackItem();

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
  void itShouldDetectSpotifyPlaylistItemMusicTestCascadeConstraintViolations() {
    // Given
    String message = "playlistItemTracks[0].trackItem: must not be null";

    SpotifyUserProfileDto userProfileItem =
       SpotifyHelper.getUserProfileItem();

    SpotifyPlaylistTrackDto playlistItemTrack =
        SpotifyPlaylistTrackDto.builder()
            .trackItem(null)
            .addedBy(userProfileItem)
            .addedAt("2020-12-04T14:14:36Z")
            .build();

    List<SpotifyPlaylistTrackDto> playlistItemTracks = List.of(playlistItemTrack);

    SpotifyPlaylistMusicDto playlistItemMusic =
        SpotifyPlaylistMusicDto.builder().playlistItemTracks(playlistItemTracks).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistMusicDto>> constraintViolations =
        validator.validate(playlistItemMusic);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

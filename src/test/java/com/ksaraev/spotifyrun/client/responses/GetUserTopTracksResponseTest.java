package com.ksaraev.spotifyrun.client.responses;

import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GetUserTopTracksResponseTest {
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                   |artist name|spotify:artist:1234567890AaBbCcDdEeFfG|51 |TRUE|trackItems[0].id: must not be null
           1234567890AaBbCcDdEeFfG|null       |spotify:artist:1234567890AaBbCcDdEeFfG|51 |TRUE|trackItems[0].name: must not be empty
           1234567890AaBbCcDdEeFfG|artist name|null                                  |51 |TRUE|trackItems[0].uri: must not be null
           1234567890AaBbCcDdEeFfG|artist name|spotify:artist:1234567890AaBbCcDdEeFfG|-1 |TRUE|trackItems[0].popularity: must be greater than or equal to 0
           1234567890AaBbCcDdEeFfG|artist name|spotify:artist:1234567890AaBbCcDdEeFfG|101|TRUE|trackItems[0].popularity: must be less than or equal to 100
           1234567890AaBbCcDdEeFfG|artist name|spotify:artist:1234567890AaBbCcDdEeFfG|51|FALSE|trackItems[0].artistItems: must not be empty
           """)
  void itShouldDetectGetUserTopTracksResponseTesConstraintViolations(
      String id, String name, URI uri, Integer popularity, Boolean hasArtists, String message) {
    // Given
    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistItem> artistItems = hasArtists ? List.of(artistItem) : List.of();

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .popularity(popularity)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> trackItems = List.of(trackItem);

    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackItems(trackItems).build();
    // When
    Set<ConstraintViolation<GetUserTopTracksResponse>> constraintViolations =
        validator.validate(getUserTopTracksResponse);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}

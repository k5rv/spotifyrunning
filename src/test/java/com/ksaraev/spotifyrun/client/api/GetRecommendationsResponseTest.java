package com.ksaraev.spotifyrun.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotifyrun.client.api.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetRecommendationsResponseTest {
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void itShouldDetectGetRecommendationsResponseConstraintViolations() {
    // Given
    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(null)
            .name("track name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackItem> trackItems = List.of(trackItem);

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackItems(trackItems).build();
    // When
    Set<ConstraintViolation<GetRecommendationsResponse>> constraintViolations =
        validator.validate(getRecommendationsResponse);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("trackItems[0].id: must not be null");
  }
}

package com.suddenrun.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.suddenrun.utils.SpotifyHelper;
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
    List<SpotifyArtistDto> artistItems = SpotifyHelper.getArtistItems(2);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id(null)
            .name("track name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistItems(artistItems)
            .build();

    List<SpotifyTrackDto> trackItems = List.of(trackItem);

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

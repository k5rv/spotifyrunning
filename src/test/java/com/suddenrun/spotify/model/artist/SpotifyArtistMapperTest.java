package com.suddenrun.spotify.model.artist;

import static com.suddenrun.spotify.model.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL;

import com.suddenrun.spotify.client.dto.SpotifyArtistDto;
import com.suddenrun.spotify.model.MappingSourceIsNullException;
import com.suddenrun.utils.SpotifyHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpotifyArtistMapperImpl.class})
class SpotifyArtistMapperTest {

  @Autowired SpotifyArtistMapper underTest;

  @Test
  void itShouldMapSpotifyArtistItemToArtist() {
    // Given
    SpotifyArtistDto artistItem = SpotifyHelper.getArtistItem();

    SpotifyArtistItem artist =
        SpotifyArtist.builder()
            .id(artistItem.id())
            .name(artistItem.name())
            .uri(artistItem.uri())
            .genres(artistItem.genres())
            .build();

    // Then
    Assertions.assertThat(underTest.mapToArtist(artistItem))
        .isNotNull()
        .hasOnlyFields("id", "name", "uri", "genres")
        .usingRecursiveComparison()
        .isEqualTo(artist);
  }

  @Test
  void itShouldThrowNullMappingSourceExceptionWhenArtistItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToArtist(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL);
  }
}

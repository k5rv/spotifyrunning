package com.suddenrun.mapping;

import static com.ksaraev.spotifyrun.spotify.model.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL;

import com.ksaraev.spotifyrun.client.dto.SpotifyArtistDto;
import com.ksaraev.spotifyrun.spotify.model.MappingSourceIsNullException;
import com.ksaraev.spotifyrun.spotify.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrun.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.spotifyrun.spotify.model.artist.SpotifyArtistMapper;
import com.suddenrun.utils.SpotifyHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpotifyArtistMapper.class})
class ArtistMapperTest {

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

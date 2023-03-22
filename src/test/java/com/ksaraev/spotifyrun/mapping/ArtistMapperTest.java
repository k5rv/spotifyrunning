package com.ksaraev.spotifyrun.mapping;

import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;

import com.ksaraev.spotifyrun.client.api.SpotifyArtistDto;
import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtistItem;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtistMapper;
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
    SpotifyArtistDto artistItem = getArtistItem();

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
        .isExactlyInstanceOf(NullMappingSourceException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
  }
}

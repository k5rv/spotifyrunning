package com.ksaraev.spotifyrun.mapping;

import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;

import com.ksaraev.spotifyrun.client.api.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.artist.ArtistMapper;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ArtistMapperImpl.class})
class ArtistMapperTest {

  @Autowired ArtistMapper underTest;

  @Test
  void itShouldMapSpotifyArtistItemToArtist() {
    // Given
    SpotifyArtistItem artistItem = getArtistItem();

    SpotifyArtist artist =
        Artist.builder()
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

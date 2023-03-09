package com.ksaraev.spotifyrun.mapping;


import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

import com.ksaraev.spotifyrun.client.api.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.artist.ArtistMapper;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
  void itShouldMapSpotifyArtistItemToArtist() throws Exception {
    // Given
    String id = "012345012345AABBccDDee";
    String name = "artist name";
    URI uri = URI.create("spotify:artist:012345012345AABBccDDee");
    List<String> genres = List.of("synth-pop", "dark post-punk");

    SpotifyArtist artist = Artist.builder().id(id).name(name).uri(uri).genres(genres).build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .genres(genres)
            .followers(Map.of("href", "", "total", 100000))
            .externalUrls(
                Map.of("spotify", "https://open.spotify.com/artist/012345012345AABBccDDee"))
            .href(new URL("https://api.spotify.com/v1/artists/012345012345AABBccDDee"))
            .images(
                List.of(
                    Map.of(
                        "height", 640, "width", 640, "url", new URL("https://i.scdn.co/image/1")),
                    Map.of(
                        "height", 320, "width", 320, "url", new URL("https://i.scdn.co/image/2")),
                    Map.of(
                        "height", 160, "width", 160, "url", new URL("https://i.scdn.co/image/3"))))
            .popularity(54)
            .type("artist")
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

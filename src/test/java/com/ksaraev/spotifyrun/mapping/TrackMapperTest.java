package com.ksaraev.spotifyrun.mapping;

import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;

import com.ksaraev.spotifyrun.client.api.*;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtistItem;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackMapper;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackMapperImpl;
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
@ContextConfiguration(classes = {SpotifyTrackMapperImpl.class, SpotifyArtistMapperImpl.class})
class TrackMapperTest {

  @Autowired SpotifyTrackMapper underTest;

  @Test
  void itShouldMapSpotifyTrackItemToTrack() throws Exception {
    // Given
    SpotifyArtistItem artist = getArtist();

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem track = getTrack();
    track.setArtists(artists);

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder()
            .id(artist.getId())
            .name(artist.getName())
            .uri(artist.getUri())
            .genres(artist.getGenres())
            .build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id(track.getId())
            .name(track.getName())
            .uri(track.getUri())
            .artistItems(artistItems)
            .albumItem(SpotifyAlbumDto.builder().build())
            .popularity(track.getPopularity())
            .href(new URL("https://api.spotify.com/v1/track/2222030405AaBbCcDdEeFf"))
            .track(true)
            .episode(false)
            .previewUrl(new URL("https://p.scdn.co/mp3-preview/2?cid=1"))
            .isLocal(false)
            .isPlayable(true)
            .durationMs(20000)
            .trackNumber(1)
            .discNumber(1)
            .sourceAlbumItem(SpotifyAlbumDto.builder().build())
            .availableMarkets(List.of("US"))
            .externalUrls(
                Map.of("spotify", "https://open.spotify.com/track/2222030405AaBbCcDdEeFf"))
            .externalIds(Map.of("spotify", "https://open.spotify.com/track/2222030405AaBbCcDdEeFf"))
            .type("track")
            .explicit(false)
            .build();

    // Then
    Assertions.assertThat(underTest.mapToTrack(trackItem))
        .isEqualTo(track)
        .hasOnlyFields("id", "name", "uri", "popularity", "artists");
  }

  @Test
  void itShouldMapPlaylistItemsToTracks() {
    // Given
    String artistID = "0102030405AaBbCcDdEeFf";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0102030405AaBbCcDdEeFf");

    String trackOneId = "1222030405AaBbCcDdEeFf";
    String trackOneName = "track 1 name";
    URI trackOneUri = URI.create("spotify:track:1222030405AaBbCcDdEeFf");
    Integer trackOnePopularity = 1;

    String trackTwoId = "2222030405AaBbCcDdEeFf";
    String trackTwoName = "track 2 name";
    URI trackTwoUri = URI.create("spotify:track:2222030405AaBbCcDdEeFf");
    Integer trackTwoPopularity = 2;

    SpotifyArtistItem artist = SpotifyArtist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem trackOne =
        SpotifyTrack.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(artists)
            .build();

    SpotifyTrackItem trackTwo =
        SpotifyTrack.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(artists)
            .build();

    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id("12122604372")
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItemOne =
        SpotifyTrackDto.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistItems(artistItems)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackDto trackItemTwo =
        SpotifyTrackDto.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artistItems(artistItems)
            .build();

    String addedAt = "2020-12-04T14:14:36Z";

    SpotifyPlaylistTrackDto playlistItemTrackOne =
        SpotifyPlaylistTrackDto.builder()
            .trackItem(trackItemOne)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    SpotifyPlaylistTrackDto playlistItemTrackTwo =
        SpotifyPlaylistTrackDto.builder()
            .trackItem(trackItemTwo)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    List<SpotifyPlaylistTrackDto> playlistItemTracks =
        List.of(playlistItemTrackOne, playlistItemTrackTwo);

    // Then
    Assertions.assertThat(underTest.mapPlaylistItemsToTracks(playlistItemTracks))
        .containsExactly(trackOne, trackTwo);
  }

  @Test
  void itShouldMapSpotifyTrackItemsToTracks() {
    // Given
    String artistID = "0102030405AaBbCcDdEeFf";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0102030405AaBbCcDdEeFf");

    String trackOneId = "1222030405AaBbCcDdEeFf";
    String trackOneName = "track 1 name";
    URI trackOneUri = URI.create("spotify:track:1222030405AaBbCcDdEeFf");
    Integer trackOnePopularity = 1;

    String trackTwoId = "2222030405AaBbCcDdEeFf";
    String trackTwoName = "track 2 name";
    URI trackTwoUri = URI.create("spotify:track:2222030405AaBbCcDdEeFf");
    Integer trackTwoPopularity = 2;

    SpotifyArtistItem artist = SpotifyArtist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem trackOne =
        SpotifyTrack.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(artists)
            .build();

    SpotifyTrackItem trackTwo =
        SpotifyTrack.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(artists)
            .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItemOne =
        SpotifyTrackDto.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistItems(artistItems)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackDto trackItemTwo =
        SpotifyTrackDto.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artistItems(artistItems)
            .build();

    // Then
    Assertions.assertThat(underTest.mapItemsToTracks(List.of(trackItemOne, trackItemTwo)))
        .containsExactly(trackOne, trackTwo);
  }

  @Test
  void itShouldReturnEmptyListWhenSourceIsNull() {
    Assertions.assertThat(underTest.mapItemsToTracks(null)).isEmpty();
  }
}

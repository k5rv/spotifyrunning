package com.ksaraev.spotifyrun.mapping;


import com.ksaraev.spotifyrun.client.items.*;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.model.track.TrackMapperImpl;
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
@ContextConfiguration(classes = {TrackMapperImpl.class, ArtistMapperImpl.class})
class TrackMapperTest {

  @Autowired TrackMapper underTest;

  @Test
  void itShouldMapSpotifyTrackItemToTrack() throws Exception {
    // Given
    String artistID = "0102030405AaBbCcDdEeFf";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0102030405AaBbCcDdEeFf");

    String trackId = "2222030405AaBbCcDdEeFf";
    String trackName = "track name";
    URI trackUri = URI.create("spotify:track:2222030405AaBbCcDdEeFf");
    Integer trackPopularity = 52;

    SpotifyArtist artist = Artist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtist> artists = List.of(artist);

    SpotifyTrack track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(artists)
            .build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItem =
        SpotifyTrackItem.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .artistItems(artistItems)
            .albumItem(SpotifyAlbumItem.builder().build())
            .popularity(trackPopularity)
            .href(new URL("https://api.spotify.com/v1/track/2222030405AaBbCcDdEeFf"))
            .track(true)
            .episode(false)
            .previewUrl(new URL("https://p.scdn.co/mp3-preview/2?cid=1"))
            .isLocal(false)
            .isPlayable(true)
            .durationMs(20000)
            .trackNumber(1)
            .discNumber(1)
            .sourceAlbumItem(SpotifyAlbumItem.builder().build())
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

    SpotifyArtist artist = Artist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtist> artists = List.of(artist);

    SpotifyTrack trackOne =
        Track.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(artists)
            .build();

    SpotifyTrack trackTwo =
        Track.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(artists)
            .build();

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id("12122604372")
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItemOne =
        SpotifyTrackItem.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistItems(artistItems)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackItem trackItemTwo =
        SpotifyTrackItem.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artistItems(artistItems)
            .build();

    String addedAt = "2020-12-04T14:14:36Z";

    SpotifyPlaylistItemTrack playlistItemTrackOne =
        SpotifyPlaylistItemTrack.builder()
            .trackItem(trackItemOne)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    SpotifyPlaylistItemTrack playlistItemTrackTwo =
        SpotifyPlaylistItemTrack.builder()
            .trackItem(trackItemTwo)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    List<SpotifyPlaylistItemTrack> playlistItemTracks =
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

    SpotifyArtist artist = Artist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtist> artists = List.of(artist);

    SpotifyTrack trackOne =
        Track.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(artists)
            .build();

    SpotifyTrack trackTwo =
        Track.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(artists)
            .build();

    SpotifyArtistItem artistItem =
        SpotifyArtistItem.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artistItems = List.of(artistItem);

    SpotifyTrackItem trackItemOne =
        SpotifyTrackItem.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistItems(artistItems)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackItem trackItemTwo =
        SpotifyTrackItem.builder()
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

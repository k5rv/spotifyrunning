package com.ksaraev.spotifyrun.mapping;

import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

import com.ksaraev.spotifyrun.client.api.items.*;
import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackMapperImpl;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapperImpl;
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
@ContextConfiguration(
    classes = {
      PlaylistMapperImpl.class,
      ArtistMapperImpl.class,
      TrackMapperImpl.class,
      UserMapperImpl.class
    })
class PlaylistMapperTest {

  @Autowired PlaylistMapper underTest;

  @Test
  void itShouldMapToPlaylist() throws Exception {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");
    String email = "email@mail.com";

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

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

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).uri(userUri).email(email).build();

    Playlist playlist =
        Playlist.builder()
            .id(playlistId)
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .owner(spotifyUser)
            .description(playlistDescription)
            .tracks(List.of(trackOne, trackTwo))
            .build();

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(userId)
            .displayName(userName)
            .uri(userUri)
            .email(email)
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

    SpotifyPlaylistItemMusic playlistItemMusic =
        SpotifyPlaylistItemMusic.builder().playlistItemTracks(playlistItemTracks).build();

    SpotifyPlaylistItem playlistItem =
        SpotifyPlaylistItem.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .userProfileItem(userProfileItem)
            .description(playlistDescription)
            .isPublic(isPublic)
            .isCollaborative(isCollaborative)
            .playlistItemMusic(playlistItemMusic)
            .primaryColor("any")
            .followers(Map.of("href", "", "total", 100000))
            .externalUrls(
                Map.of("spotify", "https://open.spotify.com/artist/012345012345AABBccDDee"))
            .href(new URL("https://api.spotify.com/v1/artists/012345012345AABBccDDee"))
            .type("playlist")
            .images(
                List.of(
                    Map.of(
                        "height", 640, "width", 640, "url", new URL("https://i.scdn.co/image/1")),
                    Map.of(
                        "height", 320, "width", 320, "url", new URL("https://i.scdn.co/image/2")),
                    Map.of(
                        "height", 160, "width", 160, "url", new URL("https://i.scdn.co/image/3"))))
            .build();
    // Then
    Assertions.assertThat(underTest.mapToPlaylist(playlistItem))
        .isEqualTo(playlist)
        .hasOnlyFields(
            "id",
            "name",
            "description",
            "uri",
            "tracks",
            "isCollaborative",
            "isPublic",
            "owner",
            "snapshotId");
  }

  @Test
  void itShouldMapToPlaylistItemDetails() {
    // Given
    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyPlaylistItemDetails playlistItemDetails =
        SpotifyPlaylistItemDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    // Then
    Assertions.assertThat(underTest.mapToPlaylistItemDetails(spotifyPlaylistDetails))
        .isNotNull()
        .hasOnlyFields("name", "description", "isCollaborative", "isPublic")
        .usingRecursiveComparison()
        .isEqualTo(playlistItemDetails);
  }

  @Test
  void mapToPlaylistShouldThrowWhenSpotifyPlaylistItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToPlaylist(null))
        .isExactlyInstanceOf(NullMappingSourceException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
  }

  @Test
  void mapToPlaylistItemDetailsShouldThrowWhenSpotifyPlaylistItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToPlaylistItemDetails(null))
        .isExactlyInstanceOf(NullMappingSourceException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
  }
}

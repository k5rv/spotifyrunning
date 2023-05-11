package com.suddenrun.mapping;

import static com.suddenrun.spotify.model.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL;

import com.suddenrun.client.dto.*;
import com.suddenrun.spotify.model.MappingSourceIsNullException;
import com.suddenrun.spotify.model.artist.SpotifyArtist;
import com.suddenrun.spotify.model.artist.SpotifyArtistItem;
import com.suddenrun.spotify.model.artist.SpotifyArtistMapperImpl;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylist;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapper;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistMapperImpl;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrack;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.track.SpotifyTrackMapperImpl;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfile;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileMapperImpl;
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
      SpotifyPlaylistMapperImpl.class,
      SpotifyArtistMapperImpl.class,
      SpotifyTrackMapperImpl.class,
      SpotifyUserProfileMapperImpl.class
    })
class PlaylistMapperTest {

  @Autowired SpotifyPlaylistMapper underTest;

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

    SpotifyArtistItem artist =
        SpotifyArtist.builder().id(artistID).name(artistName).uri(artistUri).build();

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

    SpotifyUserProfileItem spotifyUser =
        SpotifyUserProfile.builder().id(userId).name(userName).uri(userUri).email(email).build();

    SpotifyPlaylist playlist =
        SpotifyPlaylist.builder()
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

    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id(userId)
            .displayName(userName)
            .uri(userUri)
            .email(email)
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

    SpotifyPlaylistMusicDto playlistItemMusic =
        SpotifyPlaylistMusicDto.builder().playlistItemTracks(playlistItemTracks).build();

    SpotifyPlaylistDto playlistItem =
        SpotifyPlaylistDto.builder()
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

    SpotifyPlaylistItemDetails spotifyPlaylistDetails =
        SpotifyPlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyPlaylistDetailsDto playlistItemDetails =
        SpotifyPlaylistDetailsDto.builder()
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
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL);
  }

  @Test
  void mapToPlaylistItemDetailsShouldThrowWhenSpotifyPlaylistItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToPlaylistItemDetails(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL);
  }
}

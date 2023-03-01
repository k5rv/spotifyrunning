package com.ksaraev.spotifyrun.mapping;

import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.track.TrackMapperImpl;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapperImpl;
import com.ksaraev.spotifyrun.utils.JsonHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.List;

import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

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

  @Test
  void itShouldMapToPlaylist() {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

    String spotifyPlaylistItemJson =
        """
             {
               "id": "%s",
               "name": "%s",
               "description": "%s",
               "collaborative": %s,
               "public": "%s",
               "uri": "%s",
               "snapshot_id": "%s",
               "external_urls": {
                 "spotify": "https://open.spotify.com/playlist/0S4WIUelgktE36rVcG7ZRy"
               },
               "followers": {
                 "href": null,
                 "total": 0
               },
               "href": "https://api.spotify.com/v1/playlists/0S4WIUelgktE36rVcG7ZRy",
               "images": [],
               "owner": {
                 "display_name": "%s",
                 "external_urls": {
                   "spotify": "https://open.spotify.com/user/12122604372"
                 },
                 "href": "https://api.spotify.com/v1/users/12122604372",
                 "id": "%s",
                 "type": "user",
                 "uri": "%s"
               },
               "primary_color": null,
               "tracks": {
                 "href": "https://api.spotify.com/v1/playlists/0S4WIUelgktE36rVcG7ZRy/tracks",
                 "items": [],
                 "limit": 100,
                 "next": null,
                 "offset": 0,
                 "previous": null,
                 "total": 0
               },
               "type": "playlist"
             }
             """
            .formatted(
                playlistId,
                playlistName,
                playlistDescription,
                isCollaborative,
                isPublic,
                playlistUri,
                playlistSnapshotId,
                userName,
                userId,
                userUri);

    SpotifyUser spotifyUser = User.builder().id(userId).name(userName).uri(userUri).build();

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
            .tracks(List.of())
            .build();

    SpotifyPlaylistItem spotifyPlaylistItem =
        JsonHelper.jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class);

    Assertions.assertThat(underTest.mapToPlaylist(spotifyPlaylistItem)).isEqualTo(playlist);
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

    // Then
    Assertions.assertThat(underTest.mapToPlaylistItemDetails(spotifyPlaylistDetails))
        .isEqualTo(
            new SpotifyPlaylistItemDetails(
                isCollaborative, isPublic, playlistName, playlistDescription));
  }
}

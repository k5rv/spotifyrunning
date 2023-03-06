package com.ksaraev.spotifyrun.service.playlist;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.exception.service.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.service.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.service.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON_UTF8;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.ksaraev.spotifyrun.exception.service.AddTracksException;
import com.ksaraev.spotifyrun.exception.service.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.service.GetPlaylistException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.*;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.PlaylistService;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlaylistServiceIntegrationTest {

  public static WireMockServer mock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired private PlaylistService underTest;

  @BeforeAll
  static void setupClass() {
    mock.start();
  }

  @AfterAll
  static void clean() {
    mock.shutdown();
  }

  @AfterEach
  void after() {
    mock.resetAll();
  }

  @Test
  void itShouldCreatePlaylist() {
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

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

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

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemJson)));

    // Then
    assertThat(underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails)).isEqualTo(playlist);
  }

  @Test
  void createPlaylistShouldThrowConstraintViolationExceptionWhenSpotifyUserIsNull() {
    // Given
    String message = "createPlaylist.user: must not be null";

    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyPlaylistDetails playlistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();
    // Then
    Assertions.assertThatThrownBy(() -> underTest.createPlaylist(null, playlistDetails))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void createPlaylistShouldThrowConstraintViolationExceptionWhenSpotifyUserNotValid() {
    // Given
    String message = "createPlaylist.user.id: must not be null";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyPlaylistDetails playlistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyUser spotifyUser = User.builder().name(userName).uri(userUri).build();

    // Then
    Assertions.assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, playlistDetails))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void createPlaylistShouldThrowConstraintViolationExceptionWhenSpotifyPlaylistDetailsIsNull() {
    // Given
    String message = "createPlaylist.playlistDetails: must not be null";
    String id = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser spotifyUser = User.builder().id(id).name(userName).uri(userUri).build();

    // Then
    Assertions.assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void createPlaylistShouldThrowConstraintViolationExceptionWhenSpotifyPlaylistDetailsNotValid() {
    // Given
    String message = "createPlaylist.playlistDetails.name: must not be empty";
    String id = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyPlaylistDetails playlistDetails =
        PlaylistDetails.builder()
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyUser spotifyUser = User.builder().id(id).name(userName).uri(userUri).build();

    // Then
    Assertions.assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, playlistDetails))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void createPlaylistShouldThrowCreatePlaylistExceptionWhenSpotifyReturnsEmptyResponseBody() {
    // Given
    String message = "createPlaylist.<return value>: must not be null";

    String userId = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyUser spotifyUser = User.builder().id(userId).name(userName).uri(userUri).build();

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(responseDefinition().withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           "type": "playlist"           |"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |createPlaylist.<return value>.id: must not be null
           "id":"0S4WIUelgktE36rVcG7ZRy"|"type": "playlist"|"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |createPlaylist.<return value>.name: must not be empty
           "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"type": "playlist"                             |"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |createPlaylist.<return value>.uri: must not be null
           "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"type": "playlist"                                                      |TRUE |createPlaylist.<return value>.snapshotId: must not be null
           "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|FALSE|createPlaylist.<return value>.userProfileItem: must not be null
           """)
  void
      createPlaylistShouldThrowCreatePlaylistExceptionWhenSpotifyReturnsNotValidSpotifyPlaylistItem(
          String idJsonKeyValue,
          String nameJsonKeyValue,
          String uriJsonKeyValue,
          String snapshotIdJsonKeyValue,
          Boolean hasOwnerJsonKeyValue,
          String message) {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    String ownerJsonKeyValue =
        hasOwnerJsonKeyValue
            ? """
              "owner": {
                   "display_name": "Konstantin",
                   "external_urls": {
                     "spotify": "https://open.spotify.com/user/12122604372"
                   },
                   "href": "https://api.spotify.com/v1/users/12122604372",
                   "id": "12122604372",
                   "type": "user",
                   "uri": "spotify:user:12122604372"
                 }
               """
            : """
              "type": "playlist"
               """;

    String spotifyPlaylistItemJson =
        """
             {
               %s,
               %s,
               "description": "description",
               "collaborative": false,
               "public": false,
               %s,
               %s,
               "external_urls": {
                 "spotify": "https://open.spotify.com/playlist/0S4WIUelgktE36rVcG7ZRy"
               },
               "followers": {
                 "href": null,
                 "total": 0
               },
               "href": "https://api.spotify.com/v1/playlists/0S4WIUelgktE36rVcG7ZRy",
               "images": [],
               %s,
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
                idJsonKeyValue,
                nameJsonKeyValue,
                uriJsonKeyValue,
                snapshotIdJsonKeyValue,
                ownerJsonKeyValue);

    SpotifyUser spotifyUser = User.builder().id(userId).name(userName).uri(userUri).build();

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemJson)));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldGetPlaylist() {
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

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemJson)));
    // Then
    assertThat(underTest.getPlaylist(playlistId)).isEqualTo(playlist);
  }

  @Test
  void itShouldGetPlaylistWithTracks() {
    // Give
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    SpotifyArtist artist = Artist.builder().id(artistId).name(artistName).uri(artistUri).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    SpotifyTrack track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    String userId = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user = User.builder().id(userId).name(userName).uri(userUri).build();

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistDescription = "description";
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";
    Boolean playlistIsPublic = false;
    Boolean playlistIsCollaborative = false;

    SpotifyPlaylist playlist =
        Playlist.builder()
            .id(playlistId)
            .name(playlistName)
            .description(playlistDescription)
            .isPublic(playlistIsPublic)
            .isCollaborative(playlistIsCollaborative)
            .uri(playlistUri)
            .owner(user)
            .tracks(List.of(track))
            .snapshotId(playlistSnapshotId)
            .build();

    String spotifyPlaylistItemWithTracksJson =
        """
            {
              "collaborative":%s,
              "public":%s,
              "description":"%s",
              "external_urls":{
                "spotify":"https://open.spotify.com/playlist/2ZZjry5nxeL3iucxfxdtrw"
              },
              "followers":{
                "href":null,
                "total":0
              },
              "href":"https://api.spotify.com/v1/playlists/2ZZjry5nxeL3iucxfxdtrw",
              "id":"%s",
              "images":[
                {
                  "height":640,
                  "url":"https://i.scdn.co/image/ab67616d0000b273598f8e67ae9a1551d0ec2b62",
                  "width":640
                }
              ],
              "name":"%s",
              "snapshot_id":"%s",
              "owner":{
                "display_name":"%s",
                "external_urls":{
                  "spotify":"https://open.spotify.com/user/12122604372"
                },
                "href":"https://api.spotify.com/v1/users/12122604372",
                "id":"%s",
                "type":"user",
                "uri":"%s"
              },
              "primary_color":null,
              "tracks":{
                "href":"https://api.spotify.com/v1/playlists/2ZZjry5nxeL3iucxfxdtrw/tracks?offset=0&limit=100",
                "items":[
                  {
                    "added_at":"2023-02-28T08:15:03Z",
                    "added_by":{
                      "external_urls":{
                        "spotify":"https://open.spotify.com/user/12122604372"
                      },
                      "href":"https://api.spotify.com/v1/users/12122604372",
                      "id":"12122604372",
                      "type":"user",
                      "uri":"spotify:user:12122604372"
                    },
                    "is_local":false,
                    "primary_color":null,
                    "track":{
                      "album":{
                        "album_type":"single",
                        "artists":[
                          {
                            "external_urls":{
                              "spotify":"https://open.spotify.com/artist/1"
                            },
                            "href":"https://api.spotify.com/v1/artists/1",
                            "id":"%s",
                            "name":"%s",
                            "type":"artist",
                            "uri":"%s"
                          }
                        ],
                        "available_markets":[
                          "AD"
                        ],
                        "external_urls":{
                          "spotify":"https://open.spotify.com/album/1"
                        },
                        "href":"https://api.spotify.com/v1/albums/1",
                        "id":"1",
                        "images":[
                          {
                            "height":640,
                            "url":"https://i.scdn.co/image/ab67616d0000b273598f8e67ae9a1551d0ec2b62",
                            "width":640
                          },
                          {
                            "height":300,
                            "url":"https://i.scdn.co/image/ab67616d00001e02598f8e67ae9a1551d0ec2b62",
                            "width":300
                          },
                          {
                            "height":64,
                            "url":"https://i.scdn.co/image/ab67616d00004851598f8e67ae9a1551d0ec2b62",
                            "width":64
                          }
                        ],
                        "name":"1",
                        "release_date":"2018-10-10",
                        "release_date_precision":"day",
                        "total_tracks":1,
                        "type":"album",
                        "uri":"spotify:album:1"
                      },
                      "artists":[
                        {
                          "external_urls":{
                            "spotify":"https://open.spotify.com/artist/19v5IMYhkJR3ZZrjnt3b4y"
                          },
                          "href":"https://api.spotify.com/v1/artists/19v5IMYhkJR3ZZrjnt3b4y",
                          "id":"%s",
                          "name":"%s",
                          "type":"artist",
                          "uri":"%s"
                        }
                      ],
                      "available_markets":[
                        "AR"
                      ],
                      "disc_number":1,
                      "duration_ms":163081,
                      "episode":false,
                      "explicit":false,
                      "external_ids":{
                        "isrc":"1"
                      },
                      "external_urls":{
                        "spotify":"https://open.spotify.com/track/6YrvGK0jaDMfFKsjY4FYQN"
                      },
                      "href":"https://api.spotify.com/v1/tracks/6YrvGK0jaDMfFKsjY4FYQN",
                      "id":"%s",
                      "is_local":false,
                      "name":"%s",
                      "popularity":%s,
                      "preview_url":"https://p.scdn.co/mp3-preview/908ef738ea6f5d30090e235f12a4c892edb9f424?cid=f9e2da24684d494981191203959dd4d4",
                      "track":true,
                      "track_number":1,
                      "type":"track",
                      "uri":"%s"
                    },
                    "video_thumbnail":{
                      "url":null
                    }
                  }
                ],
                "limit":100,
                "next":null,
                "offset":0,
                "previous":null,
                "total":1
              },
              "type":"playlist",
              "uri":"%s"
            }
            """
            .formatted(
                playlistIsCollaborative,
                playlistIsPublic,
                playlistDescription,
                playlistId,
                playlistName,
                playlistSnapshotId,
                userName,
                userId,
                userUri,
                artistId,
                artistName,
                artistUri,
                artistId,
                artistName,
                artistUri,
                trackId,
                trackName,
                trackPopularity,
                trackUri,
                playlistUri);

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemWithTracksJson)));

    // Then
    assertThat(underTest.getPlaylist(playlistId)).isEqualTo(playlist);
  }

  @Test
  void getPlaylistShouldThrowConstraintViolationExceptionWhenPlaylistIdIsNull() {
    // Given
    String message = "getPlaylist.playlistId: must not be null";

    // Then
    Assertions.assertThatThrownBy(() -> underTest.getPlaylist(null))
        .isExactlyInstanceOf(ConstraintViolationException.class)
        .hasMessage(message);
  }

  @Test
  void getPlaylistShouldThrowGetPlaylistExceptionWhenSpotifyReturnsEmptyResponseBody() {
    // Given
    String message = "getPlaylist.<return value>: must not be null";
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(responseDefinition().withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)));
    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   "type": "playlist"           |"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |getPlaylist.<return value>.id: must not be null
                   "id":"0S4WIUelgktE36rVcG7ZRy"|"type": "playlist"|"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |getPlaylist.<return value>.name: must not be empty
                   "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"type": "playlist"                             |"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |getPlaylist.<return value>.uri: must not be null
                   "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"type": "playlist"                                                      |TRUE |getPlaylist.<return value>.snapshotId: must not be null
                   "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|FALSE|getPlaylist.<return value>.userProfileItem: must not be null
                   """)
  void getPlaylistShouldThrowGetPlaylistExceptionWhenSpotifyReturnsNotValidSpotifyPlaylistItem(
      String idJsonKeyValue,
      String nameJsonKeyValue,
      String uriJsonKeyValue,
      String snapshotIdJsonKeyValue,
      Boolean hasOwnerJsonKeyValue,
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    String ownerJsonKeyValue =
        hasOwnerJsonKeyValue
            ? """
              "owner": {
                   "display_name": "Konstantin",
                   "external_urls": {
                     "spotify": "https://open.spotify.com/user/12122604372"
                   },
                   "href": "https://api.spotify.com/v1/users/12122604372",
                   "id": "12122604372",
                   "type": "user",
                   "uri": "spotify:user:12122604372"
                 }
               """
            : """
              "type": "playlist"
               """;

    String spotifyPlaylistItemJson =
        """
                 {
                   %s,
                   %s,
                   "description": "description",
                   "collaborative": false,
                   "public": false,
                   %s,
                   %s,
                   "external_urls": {
                     "spotify": "https://open.spotify.com/playlist/0S4WIUelgktE36rVcG7ZRy"
                   },
                   "followers": {
                     "href": null,
                     "total": 0
                   },
                   "href": "https://api.spotify.com/v1/playlists/0S4WIUelgktE36rVcG7ZRy",
                   "images": [],
                   %s,
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
                idJsonKeyValue,
                nameJsonKeyValue,
                uriJsonKeyValue,
                snapshotIdJsonKeyValue,
                ownerJsonKeyValue);

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(spotifyPlaylistItemJson)));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void itShouldAddTracks() {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");

    SpotifyPlaylist playlist =
        Playlist.builder().id(playlistId).name(playlistName).uri(playlistUri).owner(user).build();

    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> tracks = List.of(track);

    String addItemsResponseJson =
        """
         {
           "snapshot_id":"Myw5YTAzNWZjNjJkMGFiMzg0NTQwYmMyZTg5MGVjNTUwN2Y1ZGQ3NTYy"
         }
         """;

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(addItemsResponseJson)));

    // When
    underTest.addTracks(playlist, tracks);

    // Then
    verify(
        postRequestedFor(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
            .withRequestBody(equalToJson("{\"uris\":[\"spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj\"]}")));
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyReturnsEmptyResponseBody() {
    // Given
    String message = "addItemsToPlaylist.<return value>: must not be null";
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");

    SpotifyPlaylist playlist =
        Playlist.builder().id(playlistId).name(playlistName).uri(playlistUri).owner(user).build();

    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> tracks = List.of(track);

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddTracksException.class)
        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyReturnsNotValidAddItemsResponse() {
    // Given
    String message = "addItemsToPlaylist.<return value>.snapshotId: must not be empty";
    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");

    SpotifyPlaylist playlist =
        Playlist.builder().id(playlistId).name(playlistName).uri(playlistUri).owner(user).build();

    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> tracks = List.of(track);

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody("{\"snapshot_id\":\"\"}")));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddTracksException.class)
        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }
}

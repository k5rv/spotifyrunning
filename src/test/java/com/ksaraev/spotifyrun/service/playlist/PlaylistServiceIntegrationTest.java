package com.ksaraev.spotifyrun.service.playlist;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.responses.AddItemsResponse;
import com.ksaraev.spotifyrun.exception.service.AddTracksException;
import com.ksaraev.spotifyrun.exception.service.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.service.GetPlaylistException;
import com.ksaraev.spotifyrun.exception.spotify.ForbiddenException;
import com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException;
import com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.PlaylistService;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.exception.service.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.service.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.service.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.spotify.ForbiddenException.FORBIDDEN;
import static com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException.TOO_MANY_REQUESTS;
import static com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException.UNAUTHORIZED;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON_UTF8;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlaylistServiceIntegrationTest {

  public static WireMockServer mock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired private PlaylistService underTest;

  @Autowired private SpotifyClient spotifyClient;

  @Captor private ArgumentCaptor<AddItemsResponse> addItemsResponseArgumentCaptor;

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

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           {"error":{"status":401,"message":"Unauthorized"}}
           {"error":"invalid_client","error_description":"Invalid client secret"}
           plain text
           ""
           """)
  void createPlaylistShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs401(
      String message) {
    // Given
    String userId = "12122604372";
    SpotifyUser spotifyUser =
        User.builder()
            .id(userId)
            .name("Konstantin")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    SpotifyPlaylistDetails spotifyPlaylistDetails = PlaylistDetails.builder().name("name").build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(401)));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           {"error":{"status":403,"message":"Forbidden"}}
           {"error":"invalid_client","error_description":"Invalid client secret"}
           plain text
           ""
           """)
  void createPlaylistShouldThrowForbiddenExceptionWhenSpotifyResponseHttpStatusCodeIs403(
      String message) {
    // Given
    String userId = "12122604372";
    SpotifyUser spotifyUser =
        User.builder()
            .id(userId)
            .name("Konstantin")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    SpotifyPlaylistDetails spotifyPlaylistDetails = PlaylistDetails.builder().name("name").build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(403)));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(ForbiddenException.class)
        .hasMessage(FORBIDDEN + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           {"error":{"status":429,"message":"Too Many Requests"}}
           {"error":"invalid_client","error_description":"Invalid client secret"}
           plain text
           ""
           """)
  void createPlaylistShouldThrowTooManyRequestsExceptionWhenSpotifyResponseHttpStatusCodeIs429(
      String message) {
    // Given
    String userId = "12122604372";
    SpotifyUser spotifyUser =
        User.builder()
            .id(userId)
            .name("Konstantin")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    SpotifyPlaylistDetails spotifyPlaylistDetails = PlaylistDetails.builder().name("name").build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(429)));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(TooManyRequestsException.class)
        .hasMessage(TOO_MANY_REQUESTS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                    400|{"error":{"status":400,"message":"Bad Request"}},
                    500|{"error":{"status":500,"message":"Internal Server Error"}},
                    502|{"error":{"status":502,"message":"Bad Gateway"}},
                    503|{"error":{"status":503,"message":"Service Unavailable"}},
                    400|{"error":"invalid_client","error_description":"Invalid client secret"}
                    400|plain text
                    400|""
                    """)
  void itShouldThrowCreatePlaylistExceptionWhenSpotifyResponseHttpStatusCodeIsNot2XX(
      Integer status, String message) {
    // Given
    String userId = "12122604372";
    SpotifyUser spotifyUser =
        User.builder()
            .id(userId)
            .name("Konstantin")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    SpotifyPlaylistDetails spotifyPlaylistDetails = PlaylistDetails.builder().name("name").build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(status)));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock = """
        {"id:"100",name":"something","size":"20"}
        Plain text""")
  void itShouldThrowCreatePlaylistExceptionWhenHttpResponseBodyNotAJsonRepresentationOfPlaylistItem(
      String responseBody) {
    // Given
    String userId = "12122604372";
    SpotifyUser spotifyUser =
        User.builder()
            .id(userId)
            .name("Konstantin")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    SpotifyPlaylistDetails spotifyPlaylistDetails = PlaylistDetails.builder().name("name").build();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(
            UNABLE_TO_CREATE_PLAYLIST
                + "Error while extracting response for type [class "
                + SpotifyPlaylistItem.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
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

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   {"error":{"status":401,"message":"Unauthorized"}}
                   {"error":"invalid_client","error_description":"Invalid client secret"}
                   plain text
                   ""
                   """)
  void getPlaylistShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs401(
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(401)));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           {"error":{"status":403,"message":"Forbidden"}}
           {"error":"invalid_client","error_description":"Invalid client secret"}
           plain text
           ""
           """)
  void getPlaylistShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs403(
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(403)));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(ForbiddenException.class)
        .hasMessage(FORBIDDEN + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   {"error":{"status":401,"message":"Too Many Requests"}}
                   {"error":"invalid_client","error_description":"Invalid client secret"}
                   plain text
                   ""
                   """)
  void getPlaylistShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs429(
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(429)));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(TooManyRequestsException.class)
        .hasMessage(TOO_MANY_REQUESTS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           400|{"error":{"status":400,"message":"Bad Request"}},
           500|{"error":{"status":500,"message":"Internal Server Error"}},
           502|{"error":{"status":502,"message":"Bad Gateway"}},
           503|{"error":{"status":503,"message":"Service Unavailable"}},
           400|{"error":"invalid_client","error_description":"Invalid client secret"}
           400|plain text
           400|""
           """)
  void getPlaylistThrowGetPlaylistExceptionWhenSpotifyResponseHttpStatusCodeIsNot2XX(
      Integer status, String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(message)
                    .withStatus(status)));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock = """
        {"id:"100",name":"something","size":"20"}
        Plain text""")
  void
      itShouldThrowGetPlaylistExceptionWhenHttpResponseBodyNotAJsonRepresentationOfSpotifyPlaylistItemClass(
          String responseBody) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));
    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(
            UNABLE_TO_GET_PLAYLIST
                + "Error while extracting response for type [class "
                + SpotifyPlaylistItem.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
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

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                           {"error":{"status":401,"message":"Unauthorized"}}
                           {"error":"invalid_client","error_description":"Invalid client secret"}
                           plain text
                           ""
                           """)
  void addTracksShouldThrowUnauthorizedExceptionWhenSpotifyResponseHttpStatusCodeIs401(
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

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
                    .withBody(message)
                    .withStatus(401)));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   {"error":{"status":403,"message":"Forbidden"}}
                   {"error":"invalid_client","error_description":"Invalid client secret"}
                   plain text
                   ""
                   """)
  void addTracksShouldThrowForbiddenExceptionWhenSpotifyResponseHttpStatusCodeIs403(
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

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
                    .withBody(message)
                    .withStatus(403)));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(ForbiddenException.class)
        .hasMessage(FORBIDDEN + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   {"error":{"status":429,"message":"Too Many Requests"}}
                   {"error":"invalid_client","error_description":"Invalid client secret"}
                   plain text
                   ""
                   """)
  void addTracksShouldThrowTooManyRequestsExceptionWhenSpotifyResponseHttpStatusCodeIs429(
      String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

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
                    .withBody(message)
                    .withStatus(429)));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(TooManyRequestsException.class)
        .hasMessage(TOO_MANY_REQUESTS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           400|{"error":{"status":400,"message":"Bad Request"}},
           500|{"error":{"status":500,"message":"Internal Server Error"}},
           502|{"error":{"status":502,"message":"Bad Gateway"}},
           503|{"error":{"status":503,"message":"Service Unavailable"}},
           400|{"error":"invalid_client","error_description":"Invalid client secret"}
           400|plain text
           400|""
           """)
  void addTracksThrowsAddTracksExceptionWhenSpotifyResponseHttpStatusCodeIsNot2XX(
      Integer status, String message) {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

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
                    .withBody(message)
                    .withStatus(status)));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddTracksException.class)
        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock = """
       {"id:"100",name":"something","size":"20"}
       Plain text""")
  void
      itShouldThrowAddTracksExceptionWhenHttpResponseBodyNotAJsonRepresentationOfAddItemsResponseClass(
          String responseBody) {
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
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8)
                    .withBody(responseBody)));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddTracksException.class)
        .hasMessage(
            UNABLE_TO_ADD_TRACKS
                + "Error while extracting response for type [class "
                + AddItemsResponse.class.getCanonicalName()
                + "] and content type [application/json;charset=UTF-8]");
  }
}

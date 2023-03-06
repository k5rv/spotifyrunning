package com.ksaraev.spotifyrun.service.playlist;

import static com.ksaraev.spotifyrun.exception.service.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.service.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.service.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.AddItemsRequest;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.exception.service.AddTracksException;
import com.ksaraev.spotifyrun.exception.service.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.service.GetPlaylistException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.PlaylistService;
import com.ksaraev.spotifyrun.service.SpotifyPlaylistService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlaylistServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private PlaylistMapper playlistMapper;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<AddItemsRequest> addItemsRequestArgumentCaptor;
  private SpotifyPlaylistService underTest;
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    MockitoAnnotations.openMocks(this);
    underTest = new PlaylistService(spotifyClient, playlistMapper);
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
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

    SpotifyPlaylistItem spotifyPlaylistItem =
        jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class);

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

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

    SpotifyPlaylistItemDetails playlistItemDetails =
        new SpotifyPlaylistItemDetails(
            isCollaborative, isPublic, playlistName, playlistDescription);

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);

    given(spotifyClient.createPlaylist(spotifyUser.getId(), playlistItemDetails))
        .willReturn(spotifyPlaylistItem);

    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class))).willReturn(playlist);

    // Then
    assertThat(underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails)).isEqualTo(playlist);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null                  |name|spotify:playlist:0S4WIUelgktE36rVcG7ZRy|TRUE |id: must not be null
           0S4WIUelgktE36rVcG7ZRy|null|spotify:playlist:0S4WIUelgktE36rVcG7ZRy|TRUE |name: must not be empty
           0S4WIUelgktE36rVcG7ZRy|name|null                                   |TRUE |uri: must not be null
           0S4WIUelgktE36rVcG7ZRy|name|spotify:playlist:0S4WIUelgktE36rVcG7ZRy|FALSE|owner: must not be null
           """)
  void itShouldDetectPlaylistConstraintViolations(
      String id, String name, URI uri, Boolean hasOwner, String message) {
    // Given
    Playlist.PlaylistBuilder playlistBuilder = Playlist.builder().id(id).name(name).uri(uri);
    if (hasOwner) {
      playlistBuilder.owner(
          User.builder()
              .id("12122604372")
              .name("Konstantin")
              .email("email@gmail.com")
              .uri(URI.create("spotify:user:12122604372"))
              .build());
    }
    SpotifyPlaylist playlist = playlistBuilder.build();
    // When
    Set<ConstraintViolation<SpotifyPlaylist>> constraintViolations = validator.validate(playlist);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectPlaylistDetailsConstraintViolations() {
    // Given
    SpotifyPlaylistDetails playlistDetails = PlaylistDetails.builder().build();
    // When
    Set<ConstraintViolation<SpotifyPlaylistDetails>> constraintViolations =
        validator.validate(playlistDetails);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("name: must not be empty");
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           "type": "playlist"           |"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |id: must not be null
           "id":"0S4WIUelgktE36rVcG7ZRy"|"type": "playlist"|"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |name: must not be empty
           "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"type": "playlist"                             |"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|TRUE |uri: must not be null
           "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"type": "playlist"                                                      |TRUE |snapshotId: must not be null
           "id":"0S4WIUelgktE36rVcG7ZRy"|"name":"name"     |"uri":"spotify:playlist:0S4WIUelgktE36rVcG7ZRy"|"snapshot_id":"MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh"|FALSE|userProfileItem: must not be null
           """)
  void itShouldDetectSpotifyPlaylistItemConstraintViolations(
      String idJsonKeyValue,
      String nameJsonKeyValue,
      String uriJsonKeyValue,
      String snapshotIdJsonKeyValue,
      Boolean hasOwnerJsonKeyValue,
      String message) {
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

    SpotifyPlaylistItem spotifyPlaylistItem =
        jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItem>> constraintViolations =
        validator.validate(spotifyPlaylistItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemDetailsConstraintViolations() {
    // Given
    SpotifyPlaylistItemDetails spotifyPlaylistItemDetails =
        new SpotifyPlaylistItemDetails(false, false, "", "description");
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemDetails>> constraintViolations =
        validator.validate(spotifyPlaylistItemDetails);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("name: must not be empty");
  }

  @Test
  void itShouldCreatePlaylistExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyPlaylistItemDetails playlistItemDetails =
        new SpotifyPlaylistItemDetails(
            isCollaborative, isPublic, playlistName, playlistDescription);

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);

    given(spotifyClient.createPlaylist(spotifyUser.getId(), playlistItemDetails))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void
      itShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistItemDetailsThrowsRuntimeException() {
    // Given
    String message = "message";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreatePlaylistException.class)
        .hasMessage(UNABLE_TO_CREATE_PLAYLIST + message);
  }

  @Test
  void itShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistThrowsRuntimeException() {
    // Given
    String message = "message";

    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
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

    SpotifyPlaylistItem spotifyPlaylistItem =
        jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class);

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyPlaylistItemDetails playlistItemDetails =
        new SpotifyPlaylistItemDetails(
            isCollaborative, isPublic, playlistName, playlistDescription);

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);

    given(spotifyClient.createPlaylist(spotifyUser.getId(), playlistItemDetails))
        .willReturn(spotifyPlaylistItem);

    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class)))
        .willThrow(new RuntimeException(message));

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
    String userEmail = "email@gmail.com";
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

    SpotifyPlaylistItem spotifyPlaylistItem =
        jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class);

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

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

    given(spotifyClient.getPlaylist(playlistId)).willReturn(spotifyPlaylistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class))).willReturn(playlist);

    // Then
    assertThat(underTest.getPlaylist(playlistId)).isEqualTo(playlist);
  }

  @Test
  void getPlaylistShouldThrowGetPlaylistExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    given(spotifyClient.getPlaylist(playlistId)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetPlaylistException.class)
        .hasMessage(UNABLE_TO_GET_PLAYLIST + message);
  }

  @Test
  void
      getPlaylistShouldThrowCreatePlaylistExceptionWhenPlaylistMapperMapToPlaylistThrowsRuntimeException() {
    // Given
    String message = "message";
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

    SpotifyPlaylistItem spotifyPlaylistItem =
        jsonToObject(spotifyPlaylistItemJson, SpotifyPlaylistItem.class);

    given(spotifyClient.getPlaylist(playlistId)).willReturn(spotifyPlaylistItem);
    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class)))
        .willThrow(new RuntimeException(message));

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

    AddItemsRequest addItemsRequest = new AddItemsRequest(List.of(trackUri));

    // When
    underTest.addTracks(playlist, List.of(track));
    // Then
    then(spotifyClient)
        .should()
        .addItemsToPlaylist(
            playlistIdArgumentCaptor.capture(), addItemsRequestArgumentCaptor.capture());

    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);

    assertThat(addItemsRequestArgumentCaptor.getValue()).isEqualTo(addItemsRequest);
  }

  @Test
  void addTracksShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
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

    given(spotifyClient.addItemsToPlaylist(any(), any())).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddTracksException.class)
        .hasMessage(UNABLE_TO_ADD_TRACKS + message);
  }
}

package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.service.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.service.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.service.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.AddItemsRequest;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.service.AddTracksException;
import com.ksaraev.spotifyrun.exception.service.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.service.GetPlaylistException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.spotify.*;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.user.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlaylistServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private PlaylistMapper playlistMapper;
  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;
  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItemDetails> playlistItemDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItem> playlistItemArgumentCaptor;
  @Captor private ArgumentCaptor<PlaylistDetails> playlistDetailsArgumentCaptor;
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
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(userId)
            .displayName(userName)
            .email(userEmail)
            .uri(userUri)
            .build();

    SpotifyPlaylistItem playlistItem =
        SpotifyPlaylistItem.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .userProfileItem(userProfileItem)
            .build();

    SpotifyPlaylistItemDetails playlistItemDetails =
        SpotifyPlaylistItemDetails.builder().name(playlistName).build();

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    Playlist playlist =
        Playlist.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .owner(user)
            .build();

    PlaylistDetails playlistDetails = PlaylistDetails.builder().name(playlistName).build();

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);

    given(spotifyClient.createPlaylist(userId, playlistItemDetails)).willReturn(playlistItem);

    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class))).willReturn(playlist);

    // When
    underTest.createPlaylist(user, playlistDetails);

    // Then
    then(playlistMapper).should().mapToPlaylistItemDetails(playlistDetailsArgumentCaptor.capture());
    assertThat(playlistDetailsArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDetails);

    then(spotifyClient)
        .should()
        .createPlaylist(
            userIdArgumentCaptor.capture(), playlistItemDetailsArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(user.getId());
    assertThat(playlistItemDetailsArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistItemDetails);

    then(playlistMapper).should().mapToPlaylist(playlistItemArgumentCaptor.capture());
    assertThat(playlistItemArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistItem);
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

    String playlistName = "playlist name";

    SpotifyUser spotifyUser =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    SpotifyPlaylistDetails spotifyPlaylistDetails =
        PlaylistDetails.builder().name(playlistName).build();

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
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(userId)
            .displayName(userName)
            .email(userEmail)
            .uri(userUri)
            .build();

    SpotifyPlaylistItem playlistItem =
        SpotifyPlaylistItem.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .userProfileItem(userProfileItem)
            .build();

    SpotifyPlaylistItemDetails playlistItemDetails =
        SpotifyPlaylistItemDetails.builder().name(playlistName).build();

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    PlaylistDetails playlistDetails = PlaylistDetails.builder().name(playlistName).build();

    given(playlistMapper.mapToPlaylistItemDetails(any(SpotifyPlaylistDetails.class)))
        .willReturn(playlistItemDetails);

    given(spotifyClient.createPlaylist(userId, playlistItemDetails)).willReturn(playlistItem);

    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class)))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(user, playlistDetails))
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
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(userId)
            .displayName(userName)
            .email(userEmail)
            .uri(userUri)
            .build();

    SpotifyPlaylistItem playlistItem =
        SpotifyPlaylistItem.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .userProfileItem(userProfileItem)
            .build();

    SpotifyUser user =
        User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    Playlist playlist =
        Playlist.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .owner(user)
            .build();

    given(spotifyClient.getPlaylist(playlistId)).willReturn(playlistItem);

    given(playlistMapper.mapToPlaylist(any(SpotifyPlaylistItem.class))).willReturn(playlist);

    // When
    underTest.getPlaylist(playlistId);

    // Then
    then(spotifyClient).should().getPlaylist(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isNotNull().isEqualTo(playlistId);

    then(playlistMapper).should().mapToPlaylist(playlistItemArgumentCaptor.capture());
    assertThat(playlistItemArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistItem);
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
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(userId)
            .displayName(userName)
            .email(userEmail)
            .uri(userUri)
            .build();

    SpotifyPlaylistItem playlistItem =
        SpotifyPlaylistItem.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .userProfileItem(userProfileItem)
            .build();

    given(spotifyClient.getPlaylist(playlistId)).willReturn(playlistItem);

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
    String playlistName = "playlist name";
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");

    SpotifyPlaylist playlist =
        Playlist.builder().id(playlistId).name(playlistName).uri(playlistUri).owner(user).build();

    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;

    List<SpotifyArtist> artists = List.of(artist);

    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(artists)
            .build();

    AddItemsRequest addItemsRequest = new AddItemsRequest(List.of(trackUri));

    // When
    underTest.addTracks(playlist, List.of(track));

    // Then
    then(spotifyClient)
        .should()
        .addItemsToPlaylist(
            playlistIdArgumentCaptor.capture(), addItemsRequestArgumentCaptor.capture());

    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlist.getId());

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

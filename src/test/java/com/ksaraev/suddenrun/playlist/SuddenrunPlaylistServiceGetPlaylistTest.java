package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.exception.GetSpotifyPlaylistException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUserMapper;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SuddenrunPlaylistServiceGetPlaylistTest {

  @Mock private SuddenrunPlaylistRepository suddenrunPlaylistRepository;

  @Mock private AppPlaylistRevisionService suddenrunRevisionService;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Mock private AppTrackMapper trackMapper;

  @Captor private ArgumentCaptor<SpotifyUserProfileItem> spotifyUserProfileArgumentCaptor;

  @Captor private ArgumentCaptor<String> spotifyPlaylistIdArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistItem> spotifyPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<String> suddenrunUserIdArgumentCaptor;

  @Captor private ArgumentCaptor<SuddenrunUser> suddenrunUserArgumentCaptor;

  @Captor private ArgumentCaptor<String> suddenrunPlaylistIdArgumentCaptor;

  @Captor private ArgumentCaptor<SuddenrunPlaylist> suddenrunPlaylistArgumentCaptor;

  private AutoCloseable closeable;

  private AppPlaylistService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SuddenrunPlaylistService(
            suddenrunPlaylistRepository,
            suddenrunRevisionService,
            spotifyPlaylistService,
            spotifyPlaylistConfig,
            playlistMapper,
            userMapper,
            trackMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldGetPlaylistIfSnapshotsInSuddenrunAndSpotifyAreEqual() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    String snapshotId = suddenrunPlaylist.getSnapshotId();
    String playlistId = suddenrunPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);

    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willReturn(spotifyUserProfile);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    spotifyPlaylist.setSnapshotId(snapshotId);
    List<SpotifyPlaylistItem> userPlaylists = List.of(spotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUserProfile)).willReturn(userPlaylists);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(spotifyPlaylist);

    // When
    Optional<AppPlaylist> optionalOfActual = underTest.getPlaylist(suddenrunUser);

    // Then
    then(suddenrunPlaylistRepository)
        .should()
        .findByOwnerId(suddenrunUserIdArgumentCaptor.capture());
    assertThat(suddenrunUserIdArgumentCaptor.getValue()).isEqualTo(userId);
    then(userMapper).should().mapToItem(suddenrunUserArgumentCaptor.capture());
    assertThat(suddenrunUserArgumentCaptor.getValue()).isEqualTo(suddenrunUser);
    then(spotifyPlaylistService)
        .should()
        .getUserPlaylists(spotifyUserProfileArgumentCaptor.capture());
    assertThat(spotifyUserProfileArgumentCaptor.getValue()).isEqualTo(spotifyUserProfile);
    then(spotifyPlaylistService).should().getPlaylist(spotifyPlaylistIdArgumentCaptor.capture());
    assertThat(spotifyPlaylistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(optionalOfActual)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).isEqualTo(suddenrunPlaylist));
  }

  @Test
  void itShouldReturnEmptyOptionalIfSuddenrunPlaylistDoesNotExist() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(Optional.empty());

    // When
    Optional<AppPlaylist> optionalOfActual = underTest.getPlaylist(suddenrunUser);

    // Then
    then(suddenrunPlaylistRepository)
        .should()
        .findByOwnerId(suddenrunUserIdArgumentCaptor.capture());
    assertThat(optionalOfActual).isNotPresent();
  }

  @Test
  void itShouldReturnEmptyOptionalIfSpotifyPlaylistDoesNotExist() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    String playlistId = suddenrunPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);

    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willReturn(spotifyUserProfile);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyPlaylistItem> userPlaylists = List.of(spotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUserProfile)).willReturn(userPlaylists);

    // When
    Optional<AppPlaylist> optionalOfActual = underTest.getPlaylist(suddenrunUser);

    // Then
    then(suddenrunPlaylistRepository)
        .should()
        .findByOwnerId(suddenrunUserIdArgumentCaptor.capture());
    assertThat(suddenrunUserIdArgumentCaptor.getValue()).isEqualTo(userId);

    then(userMapper).should().mapToItem(suddenrunUserArgumentCaptor.capture());
    assertThat(suddenrunUserArgumentCaptor.getValue()).isEqualTo(suddenrunUser);

    then(spotifyPlaylistService)
        .should()
        .getUserPlaylists(spotifyUserProfileArgumentCaptor.capture());

    then(suddenrunPlaylistRepository)
        .should()
        .deleteById(suddenrunPlaylistIdArgumentCaptor.capture());
    assertThat(suddenrunPlaylistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(optionalOfActual).isNotPresent();
  }

  @Test
  void itShouldGetPlaylistIfSnapshotsInSuddenrunAndSpotifyAreNotEqual() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    String playlistId = suddenrunPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);

    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willReturn(spotifyUserProfile);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    List<SpotifyTrackItem> spotifyTrackItems = SpotifyServiceHelper.getTracks(10);
    spotifyPlaylist.setTracks(spotifyTrackItems);
    List<SpotifyPlaylistItem> userPlaylists = List.of(spotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUserProfile)).willReturn(userPlaylists);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(spotifyPlaylist);

    List<AppTrack> customTracks = SuddenrunHelper.getTracks(2);
    given(suddenrunRevisionService.reviseCustomTracks(suddenrunPlaylist, spotifyPlaylist))
        .willReturn(customTracks);

    List<AppTrack> rejectedTracks = SuddenrunHelper.getTracks(1);
    given(suddenrunRevisionService.reviseRejectedTracks(suddenrunPlaylist, spotifyPlaylist))
        .willReturn(rejectedTracks);

    given(playlistMapper.mapToEntity(spotifyPlaylist)).willReturn(suddenrunPlaylist);

    suddenrunPlaylist.setCustomTracks(customTracks);
    suddenrunPlaylist.setRejectedTracks(rejectedTracks);
    given(suddenrunPlaylistRepository.save(suddenrunPlaylist)).willReturn(suddenrunPlaylist);

    // When
    Optional<AppPlaylist> optionalOfActual = underTest.getPlaylist(suddenrunUser);

    // Then
    then(suddenrunPlaylistRepository)
        .should()
        .findByOwnerId(suddenrunUserIdArgumentCaptor.capture());
    assertThat(suddenrunUserIdArgumentCaptor.getValue()).isEqualTo(userId);

    then(userMapper).should().mapToItem(suddenrunUserArgumentCaptor.capture());
    assertThat(suddenrunUserArgumentCaptor.getValue()).isEqualTo(suddenrunUser);

    then(spotifyPlaylistService)
        .should()
        .getUserPlaylists(spotifyUserProfileArgumentCaptor.capture());
    assertThat(spotifyUserProfileArgumentCaptor.getValue()).isEqualTo(spotifyUserProfile);

    then(spotifyPlaylistService).should().getPlaylist(spotifyPlaylistIdArgumentCaptor.capture());
    assertThat(spotifyPlaylistIdArgumentCaptor.getValue()).isEqualTo(playlistId);

    then(suddenrunRevisionService)
        .should()
        .reviseCustomTracks(
            suddenrunPlaylistArgumentCaptor.capture(), spotifyPlaylistArgumentCaptor.capture());
    assertThat(suddenrunPlaylistArgumentCaptor.getValue()).isEqualTo(suddenrunPlaylist);
    assertThat(spotifyPlaylistArgumentCaptor.getValue()).isEqualTo(spotifyPlaylist);

    then(suddenrunRevisionService)
        .should()
        .reviseRejectedTracks(
            suddenrunPlaylistArgumentCaptor.capture(), spotifyPlaylistArgumentCaptor.capture());
    assertThat(suddenrunPlaylistArgumentCaptor.getValue()).isEqualTo(suddenrunPlaylist);
    assertThat(spotifyPlaylistArgumentCaptor.getValue()).isEqualTo(spotifyPlaylist);

    then(playlistMapper).should().mapToEntity(spotifyPlaylistArgumentCaptor.capture());
    assertThat(spotifyPlaylistArgumentCaptor.getValue()).isEqualTo(spotifyPlaylist);

    then(suddenrunPlaylistRepository).should().save(suddenrunPlaylistArgumentCaptor.capture());
    assertThat(suddenrunPlaylistArgumentCaptor.getValue()).isEqualTo(suddenrunPlaylist);

    assertThat(optionalOfActual)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).isEqualTo(suddenrunPlaylist));
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willReturn(spotifyUserProfile);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUserProfile))
        .willThrow(new SpotifyAccessTokenException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(suddenrunUser))
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    String playlistId = suddenrunPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willReturn(spotifyUserProfile);
    RuntimeException runtimeException = new RuntimeException(message);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUserProfile))
        .willThrow(new GetSpotifyPlaylistException(playlistId, runtimeException));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(suddenrunUser))
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasMessageContaining(playlistId);
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfSpotifyServiceThrowsRuntimeException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willReturn(spotifyUserProfile);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUserProfile))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(suddenrunUser))
        .isExactlyInstanceOf(GetSuddenrunPlaylistException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(suddenrunPlaylistRepository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(suddenrunUser)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(suddenrunUser))
        .isExactlyInstanceOf(GetSuddenrunPlaylistException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfRepositoryThrowsRuntimeException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    given(suddenrunPlaylistRepository.findByOwnerId(userId))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(suddenrunUser))
        .isExactlyInstanceOf(GetSuddenrunPlaylistException.class)
        .hasMessageContaining(message);
  }
}

package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
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

  @Mock private SuddenrunPlaylistRepository repository;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Mock private AppTrackMapper trackMapper;

  @Captor private ArgumentCaptor<SpotifyUserProfileItem> spotifyUserProfileArgumentCaptor;

  @Captor private ArgumentCaptor<String> spotifyPlaylistIdArgumentCaptor;

  @Captor private ArgumentCaptor<String> suddenrunUserIdArgumentCaptor;

  @Captor private ArgumentCaptor<SuddenrunUser> suddenrunUserArgumentCaptor;

  private AutoCloseable closeable;

  private AppPlaylistService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SuddenrunPlaylistService(
            repository,
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
  void itShouldGetPlaylist() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    String userName = suddenrunUser.getName();
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    String snapshotId = suddenrunPlaylist.getSnapshotId();
    String playlistId = suddenrunPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(suddenrunPlaylist);
    given(repository.findByOwnerId(userId)).willReturn(optionalOfPlaylist);

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
    then(repository).should().findByOwnerId(suddenrunUserIdArgumentCaptor.capture());
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
}

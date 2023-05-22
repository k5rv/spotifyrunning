package com.ksaraev.suddenrun.playlist;

import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.AppUserMapper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SuddenrunPlaylistServiceCreatePlaylistTest {

  @Mock private SuddenrunPlaylistRepository repository;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Mock private AppTrackMapper trackMapper;

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
  void itShouldCreatePlaylist() {
    // Given
    AppUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);

    given(userMapper.mapToItem(appUser)).willReturn(spotifyUserProfile);

    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    given(spotifyPlaylistConfig.getDetails()).willReturn(spotifyPlaylistDetails);

    SpotifyPlaylistItem spotifyPlaylist =
        SpotifyServiceHelper.getPlaylist(spotifyUserProfile, spotifyPlaylistDetails);
    String spotifyPlaylistId = spotifyPlaylist.getId();
    given(spotifyPlaylistService.createPlaylist(spotifyUserProfile, spotifyPlaylistDetails))
        .willReturn(spotifyPlaylist);

    given(spotifyPlaylistService.getPlaylist(spotifyPlaylistId)).willReturn(spotifyPlaylist);

    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(spotifyPlaylistId);
    given(playlistMapper.mapToEntity(spotifyPlaylist)).willReturn(suddenrunPlaylist);

    given(repository.save(suddenrunPlaylist)).willReturn(suddenrunPlaylist);

    // When
    AppPlaylist appPlaylist = underTest.createPlaylist(appUser);

    // Then
    Assertions.assertThat(appPlaylist).isEqualTo(suddenrunPlaylist);
  }
}

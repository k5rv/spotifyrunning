package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.AppUserMapper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
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
    assertThat(appPlaylist).isEqualTo(suddenrunPlaylist);
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenException() {
    // Given
    String message = "message";
    AppUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUserProfile);
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    given(spotifyPlaylistConfig.getDetails()).willReturn(spotifyPlaylistDetails);
    given(spotifyPlaylistService.createPlaylist(spotifyUserProfile, spotifyPlaylistDetails))
        .willThrow(new SpotifyAccessTokenException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(appUser))
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceException() {
    // Given
    String message = "message";
    AppUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUserProfile);
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    given(spotifyPlaylistConfig.getDetails()).willReturn(spotifyPlaylistDetails);
    given(spotifyPlaylistService.createPlaylist(spotifyUserProfile, spotifyPlaylistDetails))
        .willThrow(new SpotifyServiceException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(appUser))
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowCreateSuddenrunPlaylistExceptionIfSpotifyServiceThrowsRuntimeException() {
    // Given
    String message = "message";
    AppUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SpotifyUserProfileItem spotifyUserProfile =
        SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUserProfile);
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    given(spotifyPlaylistConfig.getDetails()).willReturn(spotifyPlaylistDetails);
    given(spotifyPlaylistService.createPlaylist(spotifyUserProfile, spotifyPlaylistDetails))
        .willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(appUser))
        .isExactlyInstanceOf(CreateSuddenrunPlaylistException.class)
        .hasMessageContaining(userId)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowCreateSuddenrunPlaylistExceptionIfUserMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    AppUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    given(userMapper.mapToItem(appUser)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(appUser))
        .isExactlyInstanceOf(CreateSuddenrunPlaylistException.class)
        .hasMessageContaining(userId)
        .hasMessageContaining(message);
  }
}

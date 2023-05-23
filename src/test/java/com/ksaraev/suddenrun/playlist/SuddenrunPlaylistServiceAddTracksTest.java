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
import com.ksaraev.suddenrun.user.AppUserMapper;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SuddenrunPlaylistServiceAddTracksTest {

  @Mock private SuddenrunPlaylistRepository suddenrunPlaylistRepository;

  @Mock private AppPlaylistRevisionService suddenrunRevisionService;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistItem> spotifyPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<SuddenrunPlaylist> suddenrunPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> spotifyTracksArgumentCapture;

  @Captor private ArgumentCaptor<List<AppTrack>> appTracksArgumentCaptor;

  @Captor private ArgumentCaptor<List<AppTrack>> rejectedTracksArgumentCaptor;

  @Captor private ArgumentCaptor<List<AppTrack>> customTracksArgumentCaptor;

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
            userMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldThrowSuddenrunAddPlaylistTracksExceptionIfSuddenrunPlaylistDoesNotExist() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    List<AppTrack> tracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    user.addPlaylist(playlist);
    String playlistId = playlist.getId();
    given(suddenrunPlaylistRepository.existsById(playlistId)).willReturn(false);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddSuddenrunPlaylistTracksException.class)
        .hasCauseExactlyInstanceOf(SuddenrunPlaylistDoesNotExistException.class)
        .hasMessageContaining(playlistId);
  }

  @Test
  void itShouldAddPlaylistTracks() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    List<AppTrack> rejectedTracks = suddenrunPlaylist.getRejectedTracks();
    List<AppTrack> customTracks = suddenrunPlaylist.getCustomTracks();
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();

    given(suddenrunPlaylistRepository.existsById(playlistId)).willReturn(true);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    List<SpotifyTrackItem> spotifyTracks = SpotifyServiceHelper.getTracks(10);
    spotifyPlaylist.setTracks(spotifyTracks);

    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(spotifyPlaylist);

    List<SpotifyTrackItem> spotifyAddTracks = SpotifyServiceHelper.getTracks(10);
    given(suddenrunRevisionService.getTracksToAdd(suddenrunTracks, spotifyTracks, rejectedTracks))
        .willReturn(spotifyAddTracks);

    List<SpotifyTrackItem> spotifyRemoveTracks = SpotifyServiceHelper.getTracks(3);
    given(suddenrunRevisionService.getTracksToRemove(suddenrunTracks, spotifyTracks, customTracks))
        .willReturn(spotifyRemoveTracks);

    String removeTracksSnapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    given(spotifyPlaylistService.removeTracks(playlistId, spotifyRemoveTracks))
        .willReturn(removeTracksSnapshotId);

    String addTracksSnapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    given(spotifyPlaylistService.addTracks(playlistId, spotifyAddTracks))
        .willReturn(addTracksSnapshotId);

    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(spotifyPlaylist);

    given(playlistMapper.mapToEntity(spotifyPlaylist)).willReturn(suddenrunPlaylist);

    given(suddenrunPlaylistRepository.save(suddenrunPlaylist)).willReturn(suddenrunPlaylist);

    // When
    AppPlaylist actual = underTest.addTracks(suddenrunPlaylist, suddenrunTracks);

    // Then
    then(suddenrunPlaylistRepository).should().existsById(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);

    then(spotifyPlaylistService).should(times(2)).getPlaylist(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);

    then(suddenrunRevisionService)
        .should()
        .getTracksToAdd(
            appTracksArgumentCaptor.capture(),
            spotifyTracksArgumentCapture.capture(),
            rejectedTracksArgumentCaptor.capture());
    assertThat(appTracksArgumentCaptor.getValue()).isEqualTo(suddenrunTracks);
    assertThat(spotifyTracksArgumentCapture.getValue()).isEqualTo(spotifyTracks);
    assertThat(rejectedTracksArgumentCaptor.getValue()).isEqualTo(rejectedTracks);

    then(suddenrunRevisionService)
        .should()
        .getTracksToRemove(
            appTracksArgumentCaptor.capture(),
            spotifyTracksArgumentCapture.capture(),
            customTracksArgumentCaptor.capture());
    assertThat(appTracksArgumentCaptor.getValue()).isEqualTo(suddenrunTracks);
    assertThat(spotifyTracksArgumentCapture.getValue()).isEqualTo(spotifyTracks);
    assertThat(customTracksArgumentCaptor.getValue()).isEqualTo(customTracks);

    then(spotifyPlaylistService)
        .should()
        .removeTracks(playlistIdArgumentCaptor.capture(), spotifyTracksArgumentCapture.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(spotifyTracksArgumentCapture.getValue()).isEqualTo(spotifyRemoveTracks);

    then(spotifyPlaylistService)
        .should()
        .addTracks(playlistIdArgumentCaptor.capture(), spotifyTracksArgumentCapture.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(spotifyTracksArgumentCapture.getValue()).isEqualTo(spotifyAddTracks);

    then(spotifyPlaylistService).should(times(2)).getPlaylist(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);

    then(playlistMapper).should().mapToEntity(spotifyPlaylistArgumentCaptor.capture());
    assertThat(spotifyPlaylistArgumentCaptor.getValue()).isEqualTo(spotifyPlaylist);

    then(suddenrunPlaylistRepository).should().save(suddenrunPlaylistArgumentCaptor.capture());
    assertThat(suddenrunPlaylistArgumentCaptor.getValue()).isEqualTo(suddenrunPlaylist);

    assertThat((SuddenrunPlaylist) actual).isEqualTo(suddenrunPlaylist);
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();
    given(suddenrunPlaylistRepository.existsById(playlistId)).willReturn(true);
    given(spotifyPlaylistService.getPlaylist(playlistId))
        .willThrow(new SpotifyAccessTokenException(message));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(suddenrunPlaylist, suddenrunTracks))
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();
    given(suddenrunPlaylistRepository.existsById(playlistId)).willReturn(true);
    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    List<SpotifyTrackItem> spotifyTracks = SpotifyServiceHelper.getTracks(10);
    spotifyPlaylist.setTracks(spotifyTracks);
    RuntimeException runtimeException = new RuntimeException(message);
    given(spotifyPlaylistService.getPlaylist(playlistId))
        .willThrow(new GetSpotifyPlaylistException(playlistId, runtimeException));

    // Then
    assertThatThrownBy(() -> underTest.addTracks(suddenrunPlaylist, suddenrunTracks))
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasCauseExactlyInstanceOf(GetSpotifyPlaylistException.class)
        .hasMessageContaining(playlistId);
  }

  @Test
  void itShouldThrowAddSuddenrunPlaylistTracksExceptionIfSpotifyServiceThrowsRuntimeException() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();
    given(suddenrunPlaylistRepository.existsById(playlistId)).willReturn(true);
    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    List<SpotifyTrackItem> spotifyTracks = SpotifyServiceHelper.getTracks(10);
    spotifyPlaylist.setTracks(spotifyTracks);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willThrow(new RuntimeException());

    // Then
    assertThatThrownBy(() -> underTest.addTracks(suddenrunPlaylist, suddenrunTracks))
        .isExactlyInstanceOf(AddSuddenrunPlaylistTracksException.class)
        .hasMessageContaining(playlistId);
  }
}

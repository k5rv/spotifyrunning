package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.exception.GetSpotifyPlaylistException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.AppUserMapper;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SuddenrunPlaylistServiceAddTracksTest {

  @Mock private SuddenrunPlaylistRepository repository;

  @Mock private AppPlaylistSynchronizationService synchronizationService;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Mock private AppTrackMapper trackMapper;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistItem> spotifyPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<SuddenrunPlaylist> suddenrunPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> spotifyTracksArgumentCapture;

  @Captor private ArgumentCaptor<List<AppTrack>> appTracksArgumentCaptor;

  @Captor private ArgumentCaptor<List<AppTrack>> rejectedTracksArgumentCaptor;

  @Captor private ArgumentCaptor<List<AppTrack>> customTracksArgumentCaptor;

  @Captor private ArgumentCaptor<AppPlaylist> targetAppPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<AppPlaylist> sourceAppPlaylistArgumentCaptor;

  private AutoCloseable closeable;

  private AppPlaylistService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SuddenrunPlaylistService(
            repository,
            synchronizationService,
            spotifyPlaylistService,
            spotifyPlaylistConfig,
            playlistMapper,
            trackMapper,
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
    given(repository.existsById(playlistId)).willReturn(false);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddSuddenrunPlaylistTracksException.class)
        .hasCauseExactlyInstanceOf(SuddenrunPlaylistDoesNotExistException.class)
        .hasMessageContaining(playlistId);
  }

  @Test
  void itShouldAddPlaylistTracks() {
    // Given
    List<AppTrack> appTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist targetAppPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    List<AppTrack> targetAppTracks = new ArrayList<>(targetAppPlaylist.getTracks());
    String playlistId = targetAppPlaylist.getId();
    given(repository.findById(playlistId)).willReturn(Optional.of(targetAppPlaylist));

    List<SpotifyTrackItem> spotifyTrackRemovals = SpotifyServiceHelper.getTracks(5);

    List<AppTrack> appTrackRemovals =
        spotifyTrackRemovals.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.getId())
                        .name(spotifyTrack.getName())
                        .build())
            .collect(Collectors.toList());

    targetAppTracks.addAll(appTrackRemovals);
    given(synchronizationService.findPlaylistNoneMatchTracks(targetAppPlaylist, appTracks))
        .willReturn(appTrackRemovals);

    given(trackMapper.mapToDtos(appTrackRemovals)).willReturn(spotifyTrackRemovals);

    String removalSnapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    given(spotifyPlaylistService.removeTracks(any(), anyList())).willReturn(removalSnapshotId);

    List<SpotifyTrackItem> spotifyTrackAdditions = SpotifyServiceHelper.getTracks(5);

    List<AppTrack> appTrackAdditions =
        spotifyTrackAdditions.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.getId())
                        .name(spotifyTrack.getName())
                        .build())
            .collect(Collectors.toList());

    given(synchronizationService.findTracksNoneMatchPlaylist(targetAppPlaylist, appTracks))
        .willReturn(appTrackAdditions);

    given(trackMapper.mapToDtos(appTrackAdditions)).willReturn(spotifyTrackAdditions);

    String additionSnapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    targetAppTracks.addAll(appTrackAdditions);
    given(spotifyPlaylistService.addTracks(any(), anyList())).willReturn(additionSnapshotId);

    SpotifyPlaylistItem sourceSpotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    sourceSpotifyPlaylist.setTracks(spotifyTrackAdditions);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(sourceSpotifyPlaylist);

    SuddenrunPlaylist sourceAppPlaylist = SuddenrunHelper.getSuddenrunPlaylist(playlistId);
    targetAppPlaylist.setTracks(targetAppTracks);
    targetAppPlaylist.setSnapshotId(additionSnapshotId);
    given(playlistMapper.mapToEntity(sourceSpotifyPlaylist)).willReturn(targetAppPlaylist);

    given(synchronizationService.updateFromSource(targetAppPlaylist, sourceAppPlaylist))
        .willReturn(sourceAppPlaylist);

    given(repository.save(sourceAppPlaylist)).willReturn(sourceAppPlaylist);

    // When
    AppPlaylist actualAppPlaylist = underTest.addTracks(targetAppPlaylist, appTracks);

    // Then
    then(synchronizationService)
        .should()
        .updateFromSource(
            targetAppPlaylistArgumentCaptor.capture(), sourceAppPlaylistArgumentCaptor.capture());
    List<AppTrack> actualAppTracks = targetAppPlaylistArgumentCaptor.getValue().getTracks();
    assertThat(actualAppTracks)
        .containsAll(appTrackAdditions)
        .doesNotContainAnyElementsOf(appTrackRemovals);

    assertThat(actualAppPlaylist.getTracks())
            .containsAll(appTrackAdditions)
            .doesNotContainAnyElementsOf(appTrackRemovals);
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
    given(repository.findById(playlistId)).willReturn(Optional.of(suddenrunPlaylist));
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
    given(repository.findById(playlistId)).willReturn(Optional.of(suddenrunPlaylist));
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
    given(repository.existsById(playlistId)).willReturn(true);
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

package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SuddenrunPlaylistSynchronizationServiceTest {

  @Mock AppTrackMapper trackMapper;

  private static final String FIND_TRACKS_NONE_MATCH = "findTracksNoneMatch";

  private static final String FIND_TRACKS_MATCH = "findTracksMatch";

  private static final String UPDATE_PREFERENCES = "updatePreferences";

  private static final String UPDATE_EXCLUSIONS = "updateExclusions";

  private AutoCloseable closeable;

  private AppPlaylistSynchronizationService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SuddenrunPlaylistSynchronizationService(trackMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldUpdatePlaylist() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = targetPlaylist.getId();
    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist(playlistId, user);
    List<AppTrack> tracks = sourcePlaylist.getTracks();
    String snapshotId = sourcePlaylist.getSnapshotId();

    // When
    AppPlaylist result = underTest.updatePlaylist(sourcePlaylist, targetPlaylist);

    // Then
    assertThat(result.getId()).isEqualTo(playlistId);
    assertThat(result.getSnapshotId()).isEqualTo(snapshotId);
    assertThat(result.getUser()).usingRecursiveComparison().isEqualTo(user);
    assertThat(result.getTracks()).isEqualTo(tracks);
  }

  @Test
  void updatePreferencesShouldAddPreferences() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    List<AppTrack> targetTracks = List.of(trackA, trackB);
    List<AppTrack> sourceTracks = List.of(trackA, trackB, trackC);
    List<AppTrack> targetPreferences = List.of();
    Method updatePreferences =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            UPDATE_PREFERENCES, List.class, List.class, List.class);
    updatePreferences.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updatePreferences.invoke(underTest, sourceTracks, targetTracks, targetPreferences);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void updatePreferencesShouldDeletePreferences() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    List<AppTrack> targetTracks = List.of(trackA, trackB, trackC, trackD);
    List<AppTrack> targetPreferences = List.of(trackC, trackD);
    List<AppTrack> sourceTracks = List.of(trackA, trackB, trackC);
    Method updatePreferences =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            UPDATE_PREFERENCES, List.class, List.class, List.class);
    updatePreferences.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updatePreferences.invoke(underTest, sourceTracks, targetTracks, targetPreferences);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void updateExclusionsShouldAddExclusions() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    List<AppTrack> targetTracks = List.of(trackA, trackB, trackC);
    List<AppTrack> targetExclusions = List.of();
    List<AppTrack> sourceTracks = List.of(trackA, trackB);
    Method updateExclusions =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            UPDATE_EXCLUSIONS, List.class, List.class, List.class);
    updateExclusions.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updateExclusions.invoke(underTest, sourceTracks, targetTracks, targetExclusions);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void updateExclusionsShouldDeleteExclusions() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    List<AppTrack> targetTracks = List.of(trackA, trackB);
    List<AppTrack> targetExclusions = List.of(trackC, trackD);
    List<AppTrack> sourceTracks = List.of(trackA, trackB, trackC);
    Method updateExclusions =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            UPDATE_EXCLUSIONS, List.class, List.class, List.class);
    updateExclusions.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updateExclusions.invoke(underTest, sourceTracks, targetTracks, targetExclusions);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackD);
  }

  @Test
  void matchShouldReturnSourceIfItEqualsToTarget() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackB, trackA);
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  void matchShouldReturnEmptyIfSourceIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of();
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackB);
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void matchShouldReturnEmptyIfTargetIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of();
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void matchShouldReturnTracksThatPresentBothInTargetAndSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackC);
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA);
  }

  @Test
  void noneMatchShouldReturnEmptyIfSourceEqualsToTarget() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackB, trackA);
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void noneMatchShouldReturnEmptyIfSourceIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of();
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackB);
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void noneMatchShouldReturnSourceIfTargetIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of();
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  void noneMatchShouldReturnTracksThatPresentOnlyInSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackC);
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB);
  }
}

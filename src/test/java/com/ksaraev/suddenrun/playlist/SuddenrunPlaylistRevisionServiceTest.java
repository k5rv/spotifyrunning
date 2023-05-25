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

class SuddenrunPlaylistRevisionServiceTest {

  @Mock AppTrackMapper trackMapper;

  private static final String FIND_TRACKS_NONE_MATCH = "findTracksNoneMatch";

  private static final String FIND_TRACKS_MATCH = "findTracksMatch";

  private AutoCloseable closeable;

  private AppPlaylistRevisionService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SuddenrunPlaylistRevisionService(trackMapper);
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
  void matchShouldReturnActualIfItEqualsToSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackB, trackA);
    Method findTracksMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  void matchShouldReturnEmptyIfActualIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of();
    List<AppTrack> source = List.of(trackA, trackB);
    Method findTracksMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void matchShouldReturnEmptyIfSourceIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of();
    Method findTracksMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void matchShouldReturnTracksThatPresentBothInActualAndSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackA, trackC);
    Method findTracksMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA);
  }

  @Test
  void noneMatchShouldReturnEmptyIfActualEqualsToSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackB, trackA);
    Method findTracksNoneMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksNoneMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void noneMatchShouldReturnEmptyIfActualIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of();
    List<AppTrack> source = List.of(trackA, trackB);
    Method findTracksNoneMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksNoneMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void noneMatchShouldReturnActualIfSourceIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of();
    Method findTracksNoneMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksNoneMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  void noneMatchShouldReturnTracksThatPresentInActualAndNotPresentInSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackA, trackC);
    Method findTracksNoneMatch =
        SuddenrunPlaylistRevisionService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result = (List<AppTrack>) findTracksNoneMatch.invoke(underTest, actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB);
  }

  @Test
  void itShouldAddNewTrackToPreferences() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");

    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    targetPlaylist.setPreferences(List.of());
    targetPlaylist.setExclusions(List.of());
    targetPlaylist.setTracks(List.of(trackA, trackB));

    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    sourcePlaylist.setPreferences(List.of());
    sourcePlaylist.setExclusions(List.of());
    sourcePlaylist.setTracks(List.of(trackA, trackB, trackC));

    // When
    AppPlaylist result = underTest.updatePlaylist(sourcePlaylist, targetPlaylist);

    // Then
    assertThat(result.getPreferences()).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void itShouldDeletePreferenceIfPreferenceRemovedFromSource() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");

    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    targetPlaylist.setExclusions(List.of());
    targetPlaylist.setPreferences(List.of(trackC, trackD));
    targetPlaylist.setTracks(List.of(trackA, trackB, trackC, trackD));

    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    sourcePlaylist.setExclusions(List.of());
    sourcePlaylist.setPreferences(List.of());
    sourcePlaylist.setTracks(List.of(trackA, trackB, trackC));

    // When
    AppPlaylist result = underTest.updatePlaylist(sourcePlaylist, targetPlaylist);

    // Then
    assertThat(result.getPreferences()).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void itShouldAddExclusions() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");

    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    targetPlaylist.setExclusions(List.of());
    targetPlaylist.setPreferences(List.of());
    targetPlaylist.setTracks(List.of(trackA, trackB, trackC));

    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    sourcePlaylist.setExclusions(List.of());
    sourcePlaylist.setPreferences(List.of());
    sourcePlaylist.setTracks(List.of(trackA, trackB));

    // When
    AppPlaylist result = underTest.updatePlaylist(sourcePlaylist, targetPlaylist);

    // Then
    assertThat(result.getExclusions()).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void itShouldDeleteExclusionsIfExclusionAddedToSource() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");

    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    targetPlaylist.setExclusions(List.of(trackC, trackD));
    targetPlaylist.setPreferences(List.of());
    targetPlaylist.setTracks(List.of(trackA, trackB));

    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    sourcePlaylist.setExclusions(List.of());
    sourcePlaylist.setPreferences(List.of());
    sourcePlaylist.setTracks(List.of(trackA, trackB, trackC));

    // When
    AppPlaylist result = underTest.updatePlaylist(sourcePlaylist, targetPlaylist);

    // Then
    assertThat(result.getExclusions()).containsExactlyInAnyOrder(trackD);
  }
}

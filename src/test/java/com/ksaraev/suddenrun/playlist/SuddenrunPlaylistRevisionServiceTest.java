package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SuddenrunPlaylistRevisionServiceTest {

  @Mock AppTrackMapper trackMapper;

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
    AppPlaylist actualPlaylist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = actualPlaylist.getId();
    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist(playlistId, user);
    List<AppTrack> tracks = sourcePlaylist.getTracks();
    String snapshotId = sourcePlaylist.getSnapshotId();

    // When
    AppPlaylist result = underTest.updatePlaylist(actualPlaylist, sourcePlaylist);

    // Then
    assertThat(result.getId()).isEqualTo(playlistId);
    assertThat(result.getSnapshotId()).isEqualTo(snapshotId);
    assertThat(result.getUser()).usingRecursiveComparison().isEqualTo(user);
    assertThat(result.getTracks()).isEqualTo(tracks);

  }

  @Test
  void matchShouldReturnActualIfItEqualsToSource() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackB, trackA);

    // When
    List<AppTrack> result = underTest.matchSource(actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  void matchShouldReturnEmptyIfActualIsEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of();
    List<AppTrack> source = List.of(trackA, trackB);

    // When
    List<AppTrack> result = underTest.matchSource(actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void matchShouldReturnEmptyIfSourceIsEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of();

    // When
    List<AppTrack> result = underTest.matchSource(actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void matchShouldReturnTracksThatPresentBothInActualAndSource() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackA, trackC);

    // When
    List<AppTrack> result = underTest.matchSource(actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA);
  }

  @Test
  void noneMatchShouldReturnEmptyIfActualEqualsToSource() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackB, trackA);

    // When
    List<AppTrack> result = underTest.noneMatchSource(actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void noneMatchShouldReturnEmptyIfActualIsEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of();
    List<AppTrack> source = List.of(trackA, trackB);

    // When
    List<AppTrack> result = underTest.noneMatchSource(actual, source);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void noneMatchShouldReturnActualIfSourceIsEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of();

    // When
    List<AppTrack> result = underTest.noneMatchSource(actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  void noneMatchShouldReturnTracksThatPresentInActualAndNotPresentInSource() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> actual = List.of(trackA, trackB);
    List<AppTrack> source = List.of(trackA, trackC);

    // When
    List<AppTrack> result = underTest.noneMatchSource(actual, source);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB);
  }
}

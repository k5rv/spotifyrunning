package com.ksaraev.spotifyrun.controller;

import static com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException.*;
import static com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException.*;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException;
import com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException;
import com.ksaraev.spotifyrun.model.spotify.*;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.SpotifyPlaylistService;
import com.ksaraev.spotifyrun.service.SpotifyRecommendationsService;
import com.ksaraev.spotifyrun.service.SpotifyUserService;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTracksService;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlaylistControllerTest {

  @Mock private SpotifyUserService userService;
  @Mock private SpotifyUserTopTracksService topTracksService;
  @Mock private SpotifyRecommendationsService recommendationsService;
  @Mock private SpotifyPlaylistService playlistService;
  @Mock private SpotifyRunPlaylistConfig playlistConfig;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyUser> userArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistDetails> playlistDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylist> playlistArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyTrackFeatures> featuresArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrack>> userTopTracksArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrack>> musicRecommendationsArgumentCaptor;

  private PlaylistController underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest =
        new PlaylistController(
            userService, topTracksService, recommendationsService, playlistService, playlistConfig);
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    SpotifyUser user = getUser();

    SpotifyTrack topTrackA = getTrack();
    SpotifyTrack topTrackB = getTrack();
    List<SpotifyTrack> userTopTracks = List.of(topTrackA, topTrackB);

    SpotifyTrack musicRecommendation = getTrack();
    List<SpotifyTrack> musicRecommendations = List.of(musicRecommendation);

    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();

    SpotifyPlaylistDetails playlistDetails = getPlaylistDetails();
    SpotifyPlaylist playlist = getPlaylist();
    playlist.setTracks(musicRecommendations);

    given(userService.getCurrentUser()).willReturn(user);
    given(topTracksService.getUserTopTracks()).willReturn(userTopTracks);
    given(playlistConfig.getSize()).willReturn(2);
    given(playlistConfig.getMusicFeatures()).willReturn(trackFeatures);
    given(playlistConfig.getDetails()).willReturn(playlistDetails);
    given(recommendationsService.getRecommendations(anyList(), any()))
        .willReturn(musicRecommendations);
    given(playlistService.createPlaylist(user, playlistDetails)).willReturn(playlist);
    given(playlistService.getPlaylist(playlist.getId())).willReturn(playlist);

    // When
    underTest.createPlaylist();

    // Then
    verify(recommendationsService, times(2))
        .getRecommendations(
            userTopTracksArgumentCaptor.capture(), featuresArgumentCaptor.capture());

    Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
        .containsExactly(List.of(topTrackA), List.of(topTrackB));

    Assertions.assertThat(featuresArgumentCaptor.getAllValues())
        .containsExactly(trackFeatures, trackFeatures);

    verify(playlistService)
        .createPlaylist(userArgumentCaptor.capture(), playlistDetailsArgumentCaptor.capture());

    Assertions.assertThat(userArgumentCaptor.getValue()).isNotNull().isEqualTo(user);

    Assertions.assertThat(playlistDetailsArgumentCaptor.getValue())
        .isNotNull()
        .isEqualTo(playlistDetails);

    verify(playlistService, times(1))
        .addTracks(playlistArgumentCaptor.capture(), musicRecommendationsArgumentCaptor.capture());

    Assertions.assertThat(musicRecommendationsArgumentCaptor.getAllValues())
        .containsExactly(musicRecommendations);

    verify(playlistService).getPlaylist(playlistIdArgumentCaptor.capture());

    Assertions.assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlist.getId());
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   3|0|0|1|FALSE|FALSE|3
                   2|1|0|2|TRUE |FALSE|3
                   2|0|1|3|FALSE|TRUE |3
                   """)
  void itShouldStopAddMusicRecommendationsWhenRecommendationsSizeIsEqualToPlaylistConfigSize(
      Integer aTracksNumber,
      Integer bTracksNumber,
      Integer cTracksNumber,
      Integer requestsNumber,
      Boolean hasAAndBTracks,
      Boolean hasAllTracks,
      Integer playlistConfigSize) {
    // Given
    SpotifyUser user = getUser();

    SpotifyTrack topTrackA = getTrack();
    SpotifyTrack topTrackB = getTrack();
    SpotifyTrack topTrackC = getTrack();

    List<SpotifyTrack> userTopTracks = List.of(topTrackA, topTrackB, topTrackC);

    List<SpotifyTrack> musicRecommendationsA = getTracks(aTracksNumber);
    List<SpotifyTrack> musicRecommendationsB = getTracks(bTracksNumber);
    List<SpotifyTrack> musicRecommendationsC = getTracks(cTracksNumber);

    SpotifyTrackFeatures trackFeatures = getSpotifyTrackFeatures();
    SpotifyPlaylistDetails playlistDetails = getPlaylistDetails();
    SpotifyPlaylist playlist = getPlaylist();

    List<SpotifyTrack> playlistTracks = new ArrayList<>(musicRecommendationsA);

    if (hasAAndBTracks) {
      playlistTracks.addAll(musicRecommendationsB);
    }

    if (hasAllTracks) {
      playlistTracks.addAll(musicRecommendationsB);
      playlistTracks.addAll(musicRecommendationsC);
    }

    given(userService.getCurrentUser()).willReturn(user);
    given(topTracksService.getUserTopTracks()).willReturn(userTopTracks);
    given(playlistConfig.getSize()).willReturn(playlistConfigSize);
    given(playlistConfig.getMusicFeatures()).willReturn(trackFeatures);
    given(playlistConfig.getDetails()).willReturn(playlistDetails);

    given(recommendationsService.getRecommendations(List.of(topTrackA), trackFeatures))
        .willReturn(musicRecommendationsA);

    given(recommendationsService.getRecommendations(List.of(topTrackB), trackFeatures))
        .willReturn(musicRecommendationsB);

    given(recommendationsService.getRecommendations(List.of(topTrackC), trackFeatures))
        .willReturn(musicRecommendationsC);

    given(playlistService.createPlaylist(user, playlistDetails)).willReturn(playlist);

    // When
    underTest.createPlaylist();

    // Then
    verify(recommendationsService, times(requestsNumber))
        .getRecommendations(
            userTopTracksArgumentCaptor.capture(), featuresArgumentCaptor.capture());

    if (!hasAAndBTracks && !hasAllTracks) {
      Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
          .containsExactly(List.of(topTrackA));
    }

    if (hasAAndBTracks) {
      Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
          .containsExactly(List.of(topTrackA), List.of(topTrackB));
    }

    if (hasAllTracks) {
      Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
          .containsExactly(List.of(topTrackA), List.of(topTrackB), List.of(topTrackC));
    }

    verify(playlistService, times(1))
        .addTracks(playlistArgumentCaptor.capture(), musicRecommendationsArgumentCaptor.capture());

    Assertions.assertThat(musicRecommendationsArgumentCaptor.getValue())
        .isNotEmpty()
        .hasSize(playlistConfigSize)
        .hasSameElementsAs(playlistTracks);
  }

  @Test
  void itShouldThrowUserTopTracksNotFoundExceptionWhenUserTopTracksIsEmpty() {
    // Given
    given(userService.getCurrentUser()).willReturn(User.builder().build());
    given(topTracksService.getUserTopTracks()).willReturn(List.of());
    // Then
    Assertions.assertThatThrownBy(() -> underTest.createPlaylist())
        .isExactlyInstanceOf(UserTopTracksNotFoundException.class)
        .hasMessage(USER_TOP_TRACKS_NOT_FOUND);
  }

  @Test
  void itShouldThrowRecommendationsNotFoundExceptionWhenMusicRecommendationsIsEmpty() {
    // Given
    given(userService.getCurrentUser()).willReturn(User.builder().build());
    given(topTracksService.getUserTopTracks()).willReturn(List.of(Track.builder().build()));
    given(recommendationsService.getRecommendations(anyList(), any())).willReturn(List.of());
    // Then
    Assertions.assertThatThrownBy(() -> underTest.createPlaylist())
        .isExactlyInstanceOf(RecommendationsNotFoundException.class)
        .hasMessage(RECOMMENDATIONS_NOT_FOUND);
  }
}

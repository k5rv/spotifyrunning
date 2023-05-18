package com.suddenrun.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.suddenrun.app.playlist.AppPlaylistConfig;
import com.suddenrun.app.playlist.AppPlaylistService;
import com.suddenrun.app.playlist.PlaylistController;
import com.suddenrun.app.track.AppTrackService;
import com.suddenrun.app.user.AppUserService;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrack;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfile;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.spotify.service.*;
import com.suddenrun.utils.SpotifyHelper;
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

  @Mock private SpotifyUserProfileItemService userService;
  @Mock private SpotifyUserTopTrackItemsService topTracksService;
  @Mock private SpotifyRecommendationItemsService recommendationsService;
  @Mock private SpotifyPlaylistItemService playlistService;
  @Mock private AppPlaylistConfig playlistConfig;

  @Mock private AppUserService appUserService;
  @Mock private AppPlaylistService appPlaylistService;
  @Mock private AppTrackService appTrackService;

  @Mock private SpotifyUserProfileService userProfileService;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyUserProfileItem> userArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItemDetails> playlistDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItem> playlistArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyTrackItemFeatures> featuresArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> userTopTracksArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> musicRecommendationsArgumentCaptor;

  private PlaylistController underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new PlaylistController(  appUserService, appTrackService, appPlaylistService, userProfileService);
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();

    SpotifyTrackItem topTrackA = SpotifyHelper.getTrack();
    SpotifyTrackItem topTrackB = SpotifyHelper.getTrack();
    List<SpotifyTrackItem> userTopTracks = List.of(topTrackA, topTrackB);

    SpotifyTrackItem musicRecommendation = SpotifyHelper.getTrack();
    List<SpotifyTrackItem> musicRecommendations = List.of(musicRecommendation);

    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();

    SpotifyPlaylistItemDetails playlistDetails = SpotifyHelper.getPlaylistDetails();
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();
    playlist.setTracks(musicRecommendations);

    given(userService.getCurrentUserProfile()).willReturn(user);
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

//    verify(playlistService, times(1))
//        .addTracks(playlistArgumentCaptor.capture(), musicRecommendationsArgumentCaptor.capture());

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
    SpotifyUserProfileItem user = SpotifyHelper.getUserProfile();

    SpotifyTrackItem topTrackA = SpotifyHelper.getTrack();
    SpotifyTrackItem topTrackB = SpotifyHelper.getTrack();
    SpotifyTrackItem topTrackC = SpotifyHelper.getTrack();

    List<SpotifyTrackItem> userTopTracks = List.of(topTrackA, topTrackB, topTrackC);

    List<SpotifyTrackItem> musicRecommendationsA = SpotifyHelper.getTracks(aTracksNumber);
    List<SpotifyTrackItem> musicRecommendationsB = SpotifyHelper.getTracks(bTracksNumber);
    List<SpotifyTrackItem> musicRecommendationsC = SpotifyHelper.getTracks(cTracksNumber);

    SpotifyTrackItemFeatures trackFeatures = SpotifyHelper.getSpotifyTrackFeatures();
    SpotifyPlaylistItemDetails playlistDetails = SpotifyHelper.getPlaylistDetails();
    SpotifyPlaylistItem playlist = SpotifyHelper.getPlaylist();

    List<SpotifyTrackItem> playlistTracks = new ArrayList<>(musicRecommendationsA);

    if (hasAAndBTracks) {
      playlistTracks.addAll(musicRecommendationsB);
    }

    if (hasAllTracks) {
      playlistTracks.addAll(musicRecommendationsB);
      playlistTracks.addAll(musicRecommendationsC);
    }

    given(userService.getCurrentUserProfile()).willReturn(user);
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

//    verify(playlistService, times(1))
//        .addTracks(playlistArgumentCaptor.capture(), musicRecommendationsArgumentCaptor.capture());

    Assertions.assertThat(musicRecommendationsArgumentCaptor.getValue())
        .isNotEmpty()
        .hasSize(playlistConfigSize)
        .hasSameElementsAs(playlistTracks);
  }

  @Test
  void itShouldThrowUserTopTracksNotFoundExceptionWhenUserTopTracksIsEmpty() {
    // Given
    given(userService.getCurrentUserProfile()).willReturn(SpotifyUserProfile.builder().build());
    given(topTracksService.getUserTopTracks()).willReturn(List.of());
    // Then
//    Assertions.assertThatThrownBy(() -> underTest.createPlaylist())
//        .isExactlyInstanceOf(UserTopTracksNotFoundException.class)
//        .hasMessage(USER_TOP_TRACKS_NOT_FOUND);
  }

  @Test
  void itShouldThrowRecommendationsNotFoundExceptionWhenMusicRecommendationsIsEmpty() {
    // Given
    given(userService.getCurrentUserProfile()).willReturn(SpotifyUserProfile.builder().build());
    given(topTracksService.getUserTopTracks()).willReturn(List.of(SpotifyTrack.builder().build()));
    given(recommendationsService.getRecommendations(anyList(), any())).willReturn(List.of());
    // Then
//    Assertions.assertThatThrownBy(() -> underTest.createPlaylist())
//        .isExactlyInstanceOf(RecommendationsNotFoundException.class)
//        .hasMessage(RECOMMENDATIONS_NOT_FOUND);
  }
}

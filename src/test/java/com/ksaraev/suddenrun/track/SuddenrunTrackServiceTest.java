package com.ksaraev.suddenrun.track;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.ksaraev.spotify.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotify.service.SpotifyUserTopTrackItemsService;
import com.ksaraev.suddenrun.playlist.AppPlaylistConfig;
import com.ksaraev.suddenrun.user.AppUserService;
import com.ksaraev.suddenrun.user.SuddenrunUserService;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.mockito.BDDMockito.*;

public class SuddenrunTrackServiceTest {

  @Mock private AppPlaylistConfig config;

  @Mock private AppTrackMapper mapper;

  @Mock private SpotifyUserTopTrackItemsService spotifyTopTracksService;

  @Mock private SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> trackItemsArgumentCaptor;

  private AutoCloseable closeable;

  private AppTrackService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SuddenrunTrackService(
            config, mapper, spotifyTopTracksService, spotifyRecommendationsService);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldReturnTracks() {
    // Given
    int playlistSize = 10;
    given(config.getSize()).willReturn(playlistSize);

    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    given(config.getMusicFeatures()).willReturn(features);

    SpotifyTrackItem trackItem = SpotifyServiceHelper.getTrack();
    List<SpotifyTrackItem> userTopTracks = List.of(trackItem);
    given(spotifyTopTracksService.getUserTopTracks()).willReturn(userTopTracks);

    int recommendationsNumber = 10;
    List<SpotifyTrackItem> recommendations = SpotifyServiceHelper.getTracks(recommendationsNumber);
    given(spotifyRecommendationsService.getRecommendations(userTopTracks, features))
        .willReturn(recommendations);

    List<AppTrack> appTracks = SuddenrunHelper.getTracks(playlistSize);
    given(mapper.mapToEntities(any())).willReturn(appTracks);

    // When
    List<AppTrack> tracks = underTest.getTracks();

    // Then
    then(mapper).should().mapToEntities(trackItemsArgumentCaptor.capture());
    List<SpotifyTrackItem> trackItemsArgumentCaptorValue = trackItemsArgumentCaptor.getValue();
    Assertions.assertThat(trackItemsArgumentCaptorValue)
        .isNotNull()
        .hasSameElementsAs(recommendations);
    Assertions.assertThat(tracks).hasSameElementsAs(appTracks);
  }
}

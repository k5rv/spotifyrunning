package com.ksaraev.spotifyrun.service.playlist;

import static com.ksaraev.spotifyrun.exception.controller.UserTopTracksNotFoundException.USER_TOP_TRACKS_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.controller.PlaylistController;
import com.ksaraev.spotifyrun.exception.controller.RecommendationsNotFoundException;
import com.ksaraev.spotifyrun.exception.controller.UserTopTracksNotFoundException;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.*;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.service.SpotifyPlaylistService;
import com.ksaraev.spotifyrun.service.SpotifyRecommendationsService;
import com.ksaraev.spotifyrun.service.SpotifyUserService;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTracksService;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlaylistControllerTest {

  @Mock SpotifyUserService userService;
  @Mock SpotifyUserTopTracksService topTracksService;
  @Mock SpotifyRecommendationsService recommendationsService;
  @Mock SpotifyPlaylistService playlistService;
  @Mock SpotifyRunPlaylistConfig playlistConfig;

  PlaylistController underTest;

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
    SpotifyUser user =
        User.builder()
            .id("12122604372")
            .name("Konstantin")
            .email("email@gmail.com")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    given(userService.getCurrentUser()).willReturn(user);

    SpotifyArtist artist =
        Artist.builder()
            .id("5VnrVRYzaatWXs102ScGwN")
            .name("name")
            .uri(URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN"))
            .build();

    SpotifyTrack topTrackA =
        Track.builder()
            .id("5Ko5Jn0OG8IDFEHhAYsCnj")
            .name("name_a")
            .uri(URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj"))
            .popularity(32)
            .artists(List.of(artist))
            .build();

    SpotifyTrack topTrackB =
        Track.builder()
            .id("5Ko5Jn0OG8IDFEHhAYsCn1")
            .name("name_b")
            .uri(URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCn1"))
            .popularity(20)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> userTopTracks = List.of(topTrackA, topTrackB);

    given(topTracksService.getUserTopTracks()).willReturn(userTopTracks);

    SpotifyTrack musicRecommendation =
        Track.builder()
            .id("6Ko5Jn0OG8IDFEHhAYsCn1")
            .name("recommendations")
            .uri(URI.create("spotify:track:6Ko5Jn0OG8IDFEHhAYsCn1"))
            .popularity(90)
            .artists(List.of(artist))
            .build();

    List<SpotifyTrack> musicRecommendations = List.of(musicRecommendation);

    SpotifyTrackFeatures trackFeatures =
        TrackFeatures.builder().minTempo(new BigDecimal(120)).build();

    SpotifyPlaylistDetails playlistDetails = PlaylistDetails.builder().name("name").build();

    given(playlistConfig.getSizeLimit()).willReturn(2);
    given(playlistConfig.getMusicFeatures()).willReturn(trackFeatures);
    given(playlistConfig.getDetails()).willReturn(playlistDetails);
    given(recommendationsService.getRecommendations(anyList(), any()))
        .willReturn(musicRecommendations);

    SpotifyPlaylist playlist =
        Playlist.builder()
            .id("0S4WIUelgktE36rVcG7ZRy")
            .name("name")
            .uri(URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy"))
            .description("description")
            .snapshotId("MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh")
            .isCollaborative(false)
            .isPublic(false)
            .build();

    given(playlistService.createPlaylist(user, playlistDetails)).willReturn(playlist);

    playlist.setTracks(musicRecommendations);

    given(playlistService.getPlaylist(playlist.getId())).willReturn(playlist);

    // Then
    Assertions.assertThat(underTest.createPlaylist()).isNotNull().isEqualTo(playlist);
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
        .hasMessage(RecommendationsNotFoundException.RECOMMENDATIONS_NOT_FOUND);
  }
}

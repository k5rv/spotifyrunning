package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException.*;
import static com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException.*;

import com.ksaraev.spotifyrun.app.runner.SpotifyAppUserService;
import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException;
import com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.service.SpotifyPlaylistItemService;
import com.ksaraev.spotifyrun.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotifyrun.service.SpotifyUserProfileItemService;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTrackItemsService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class PlaylistController {
  private final SpotifyUserProfileItemService userService;
  private final SpotifyUserTopTrackItemsService topTracksService;
  private final SpotifyRecommendationItemsService recommendationsService;
  private final SpotifyPlaylistItemService playlistService;
  private final SpotifyRunPlaylistConfig playlistConfig;

  private final SpotifyAppUserService spotifyAppUserService;

  @PostMapping
  public SpotifyPlaylistItem createPlaylist() {
    SpotifyUserProfileItem spotifyUserProfile = userService.getCurrentUser();
    if (!spotifyAppUserService.isUserRegistered(spotifyUserProfile)) {
      spotifyAppUserService.registerUser(spotifyUserProfile);
    }

    List<SpotifyTrackItem> topTracks = topTracksService.getUserTopTracks();
    if (topTracks.isEmpty()) {
      throw new UserTopTracksNotFoundException(USER_TOP_TRACKS_NOT_FOUND);
    }

    List<SpotifyTrackItem> musicRecommendations =
        topTracks.stream()
            .map(
                track ->
                    recommendationsService.getRecommendations(
                        List.of(track), playlistConfig.getMusicFeatures()))
            .flatMap(List::stream)
            .distinct()
            .limit(playlistConfig.getSize())
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                      Collections.shuffle(list);
                      return list;
                    }));

    if (musicRecommendations.isEmpty()) {
      throw new RecommendationsNotFoundException(RECOMMENDATIONS_NOT_FOUND);
    }

    return null;

    //
    //

    //

    //

    //
    //    SpotifyPlaylistItem playlist = playlistService.createPlaylist(user,
    // playlistConfig.getDetails());
    //    playlistService.addTracks(playlist, musicRecommendations);
    //    return playlistService.getPlaylist(playlist.getId());

  }
}

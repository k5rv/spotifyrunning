package com.ksaraev.spotifyrun.controller;

import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException.*;
import static com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException.*;

import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException;
import com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.service.SpotifyPlaylistService;
import com.ksaraev.spotifyrun.service.SpotifyRecommendationsService;
import com.ksaraev.spotifyrun.service.SpotifyUserService;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTracksService;
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
  private final SpotifyUserService userService;
  private final SpotifyUserTopTracksService topTracksService;
  private final SpotifyRecommendationsService recommendationsService;
  private final SpotifyPlaylistService playlistService;
  private final SpotifyRunPlaylistConfig playlistConfig;

  @PostMapping
  public SpotifyPlaylist createPlaylist() {
    SpotifyUser user = userService.getCurrentUser();
    List<SpotifyTrack> userTopTracks = topTracksService.getUserTopTracks();

    if (userTopTracks.isEmpty()) {
      throw new UserTopTracksNotFoundException(USER_TOP_TRACKS_NOT_FOUND);
    }

    // TODO: check that requests continue while list size less than playlistConfig.getSize()
    List<SpotifyTrack> musicRecommendations =
        userTopTracks.stream()
            .map(
                userTopTrack ->
                    recommendationsService.getRecommendations(
                        List.of(userTopTrack), playlistConfig.getMusicFeatures()))
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

    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistConfig.getDetails());
    playlistService.addTracks(playlist, musicRecommendations);
    return playlistService.getPlaylist(playlist.getId());
  }
}

package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.config.playlist.SpotifyRunningWorkoutPlaylistConfig;
import com.ksaraev.spotifyrunning.exception.CreatePlaylistException;
import com.ksaraev.spotifyrunning.model.spotify.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RunningWorkoutService implements SpotifyRunningWorkoutService {

  private final SpotifyUserService userService;
  private final SpotifyPlaylistService playlistService;
  private final SpotifyRecommendationsService recommendationsService;
  private final SpotifyRunningWorkoutPlaylistConfig runningWorkoutPlaylistConfig;

  @Override
  public SpotifyPlaylist createPlaylist(
      SpotifyPlaylistDetails playlistDetails, SpotifyTrackFeatures trackFeatures) {

    SpotifyUser user = userService.getUser();

    log.info("Getting user {} top tracks", user.getId());
    List<SpotifyTrack> userTopTracks = userService.getTopTracks();
    if (CollectionUtils.isEmpty(userTopTracks)) {
      throw new CreatePlaylistException(
          "Unable to create playlist. User "
              + user.getId()
              + " top tracks required for a seed were not found.");
    }
    log.info("Found user {} top tracks", user.getId());

    log.info("Getting user {} tracks recommendations", user.getId());
    List<SpotifyTrack> tracks =
        userTopTracks.stream()
            .map(
                userTopTrack ->
                    recommendationsService.getTracks(
                        Collections.singletonList(userTopTrack),
                        List.of(),
                        List.of(),
                        trackFeatures))
            .flatMap(List::stream)
            .distinct()
            .limit(runningWorkoutPlaylistConfig.getSizeLimit())
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                      Collections.shuffle(list);
                      return list;
                    }));
    if (CollectionUtils.isEmpty(tracks)) {
      throw new CreatePlaylistException(
          "Unable to create playlist. Recommendations based on tracks "
              + userTopTracks
              + " were not found.");
    }
    log.info("Found user {} tracks recommendations", user.getId());
    log.info("Creating playlist for user {}", user.getId());
    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistDetails);
    log.info("Created playlist {} for user {}", playlist.getId(), user.getId());
    log.info("Adding tracks to playlist {}", playlist.getId());
    playlistService.addTracks(playlist, tracks);
    log.info("Added tracks to playlist {}", playlist.getId());
    return playlistService.getPlaylist(playlist.getId());
  }
}

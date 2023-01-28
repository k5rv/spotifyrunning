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
    log.info("Found current user - userId:{}", user.getId());

    log.info("Getting top tracks - userId:{}", user.getId());
    List<SpotifyTrack> userTopTracks = userService.getTopTracks();
    if (CollectionUtils.isEmpty(userTopTracks)) {
      throw new CreatePlaylistException(
          "Unable to create playlist. User (userId:"
              + user.getId()
              + ") top tracks required for a seed were not found.");
    }
    log.info("Found top tracks - userId:{} ", user.getId());

    log.info("Getting recommendations - userId:{} ", user.getId());
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
          "Unable to create Playlist. User (userId:"
              + user.getId()
              + ") recommendations based on tracks "
              + userTopTracks.stream().map(SpotifyTrack::getName).toList()
              + " were not found.");
    }
    log.info("Found recommendations - userId:{}", user.getId());

    log.info("Creating playlist - userId:{}", user.getId());
    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistDetails);
    log.info("Created playlist - playlistId:{}, userId:{}", playlist.getId(), user.getId());

    log.info("Adding tracks - playlistId:{}", playlist.getId());
    playlistService.addTracks(playlist, tracks);
    log.info("Added tracks - playlistId:{}", playlist.getId());

    log.info("Returning playlist - playlistId:{}", playlist.getId());
    return playlistService.getPlaylist(playlist.getId());
  }
}

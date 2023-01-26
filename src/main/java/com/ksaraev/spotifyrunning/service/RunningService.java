package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.config.runningplaylist.SpotifyRunningPlaylistConfig;
import com.ksaraev.spotifyrunning.exception.CreatePlaylistException;
import com.ksaraev.spotifyrunning.model.recommendations.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
import jakarta.validation.constraints.NotNull;
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
public class RunningService implements SpotifyRunningService {

  private final SpotifyUserService userService;
  private final SpotifyPlaylistService playlistService;

  private final SpotifyRunningPlaylistConfig spotifyRunningPlaylistConfig;
  private final SpotifyRecommendationsService recommendationService;

  @Override
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyPlaylistDetails playlistDetails, SpotifyRecommendationsFeatures features) {

    SpotifyUser user = userService.getUser();

    log.info("Getting user {} top tracks", user.getId());
    List<SpotifyTrack> userTopTracks = userService.getTopTracks();
    if (CollectionUtils.isEmpty(userTopTracks)) {
      throw new CreatePlaylistException(
          "Unable to create playlist. User"
              + user.getId()
              + " top tracks required for a seed not found.");
    }
    log.info("Found user {} top tracks", user.getId());

    log.info("Getting user {} tracks recommendations", user.getId());
    List<SpotifyTrack> tracks =
        userTopTracks.stream()
            .map(
                userTopTrack ->
                    recommendationService.getTracks(
                        Collections.singletonList(userTopTrack),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        features))
            .flatMap(List::stream)
            .distinct()
            .limit(spotifyRunningPlaylistConfig.getPlaylistSizeLimit())
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                      Collections.shuffle(list);
                      return list;
                    }));
    if (CollectionUtils.isEmpty(tracks)) {
      throw new CreatePlaylistException("Tracks recommendations not found");
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

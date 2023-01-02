package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.config.runningplaylist.SpotifyRunningPlaylistConfig;
import com.ksaraev.spotifyrunning.exception.SpotifyResourceNotFoundException;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RunningPlaylistService implements SpotifyRunningPlaylistService {

  private final SpotifyUserService userService;
  private final SpotifyPlaylistService playlistService;

  private final SpotifyRunningPlaylistConfig spotifyRunningPlaylistConfig;
  private final SpotifyRecommendationsService recommendationService;

  @Override
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyPlaylistDetails playlistDetails, SpotifyRecommendationsFeatures features) {

    SpotifyUser user = userService.getUser();

    List<SpotifyTrack> userTopTracks = userService.getTopTracks();

    if (userTopTracks.isEmpty()) {
      throw new SpotifyResourceNotFoundException(
          "User %s top tracks not found".formatted(user.getId()));
    }

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

    if (tracks.isEmpty()) {
      throw new SpotifyResourceNotFoundException(
          "User %s recommendations not found".formatted(user.getId()));
    }

    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistDetails);

    playlistService.addTracks(playlist, tracks);

    return playlistService.getPlaylist(playlist.getId());
  }
}

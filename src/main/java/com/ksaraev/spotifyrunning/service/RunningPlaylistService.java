package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.config.runningplaylist.SpotifyRunningPlaylistConfig;
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
      @NotNull SpotifyPlaylistDetails playlistDetails, SpotifyRecommendationFeatures features) {

    List<SpotifyTrack> userTopTracks = userService.getTopTracks();

    if (userTopTracks.isEmpty()) {
      throw new IllegalStateException("Top tracks not found");
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
            .toList();

    if (tracks.isEmpty()) {
      throw new IllegalStateException("Recommendations not found");
    }

    SpotifyUser user = userService.getUser();

    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistDetails);

    playlistService.addTracks(playlist, tracks);

    return playlistService.getPlaylist(playlist.getId());
  }
}

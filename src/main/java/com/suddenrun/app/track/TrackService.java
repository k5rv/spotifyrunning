package com.suddenrun.app.track;

import com.suddenrun.app.exception.AppAuthorizationException;
import com.suddenrun.app.exception.AppSpotifyServiceInteractionException;
import com.suddenrun.app.playlist.AppPlaylistConfig;
import com.suddenrun.spotify.exception.SpotifyRecommendationsServiceException;
import com.suddenrun.spotify.exception.SpotifyAccessTokenException;
import com.suddenrun.spotify.exception.GetSpotifyUserTopTracksException;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.service.SpotifyRecommendationItemsService;
import com.suddenrun.spotify.service.SpotifyUserTopTrackItemsService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackService implements AppTrackService {

  private final AppPlaylistConfig playlistConfig;

  private final AppTrackMapper appTrackMapper;

  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;

  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Override
  public List<AppTrack> getTracks() {
    try {
      int playlistSizeLimit = playlistConfig.getSize();
      List<SpotifyTrackItem> userTopTracks = spotifyTopTracksService.getUserTopTracks();
      int topTracksSize = userTopTracks.size();
      log.info("Found [" + topTracksSize + "] top tracks in Spotify");
      List<SpotifyTrackItem> recommendations =
          userTopTracks.stream()
              .map(
                  userTopTrack ->
                      spotifyRecommendationsService.getRecommendations(
                          List.of(userTopTrack), playlistConfig.getMusicFeatures()))
              .flatMap(List::stream)
              .sorted(Comparator.comparingInt(SpotifyTrackItem::getPopularity).reversed())
              .distinct()
              .limit(playlistSizeLimit)
              .collect(
                  Collectors.collectingAndThen(
                      Collectors.toList(),
                      list -> {
                        Collections.shuffle(list);
                        return list;
                      }));
      int recommendationsSize = recommendations.size();
      log.info("Found [" + recommendationsSize + "] recommended tracks in Spotify");
      return recommendations.stream()
          .filter(Objects::nonNull)
          .map(appTrackMapper::mapToEntity)
          .toList();
    } catch (SpotifyAccessTokenException e) {
      throw new AppAuthorizationException(e);
    } catch (GetSpotifyUserTopTracksException | SpotifyRecommendationsServiceException e) {
      throw new AppSpotifyServiceInteractionException(e);
    } catch (RuntimeException e) {
      throw new AppTrackServiceGetTracksException(e);
    }
  }
}

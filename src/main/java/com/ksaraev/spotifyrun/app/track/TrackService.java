package com.ksaraev.spotifyrun.app.track;

import com.ksaraev.spotifyrun.app.exception.AppAuthorizationException;
import com.ksaraev.spotifyrun.app.exception.AppSpotifyServiceInteractionException;
import com.ksaraev.spotifyrun.app.playlist.AppPlaylistConfig;
import com.ksaraev.spotifyrun.spotify.exception.SpotifyRecommendationsServiceException;
import com.ksaraev.spotifyrun.spotify.exception.SpotifyServiceAuthenticationException;
import com.ksaraev.spotifyrun.spotify.exception.SpotifyUserTopTracksServiceException;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotifyrun.spotify.service.SpotifyUserTopTrackItemsService;
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
    } catch (SpotifyServiceAuthenticationException e) {
      throw new AppAuthorizationException(e);
    } catch (SpotifyUserTopTracksServiceException | SpotifyRecommendationsServiceException e) {
      throw new AppSpotifyServiceInteractionException(e);
    } catch (RuntimeException e) {
      throw new AppTrackServiceGetTracksException(e);
    }
  }
}

package com.ksaraev.suddenrun.track;

import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotify.service.SpotifyUserTopTrackItemsService;
import com.ksaraev.suddenrun.playlist.AppPlaylistConfig;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;

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
public class SuddenrunTrackService implements AppTrackService {

  private final AppPlaylistConfig config;

  private final AppTrackMapper mapper;

  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;

  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Override
  public List<AppTrack> getTracks() {
    try {
      int playlistSizeLimit = config.getSize();
      List<SpotifyTrackItem> userTopTracks = spotifyTopTracksService.getUserTopTracks();
      int topTracksSize = userTopTracks.size();
      log.info("Found [" + topTracksSize + "] top tracks in Spotify");
      List<SpotifyTrackItem> recommendations =
          userTopTracks.stream()
              .map(
                  userTopTrack ->
                      spotifyRecommendationsService.getRecommendations(
                          List.of(userTopTrack), config.getMusicFeatures()))
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
      return mapper.mapToEntities(recommendations).stream().filter(Objects::nonNull).toList();
      //      return recommendations.stream()
      //          .filter(Objects::nonNull)
      //          .map(mapper::mapToEntity)
      //          .toList();
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new GetSuddenrunTracksException(e);
    }
  }
}

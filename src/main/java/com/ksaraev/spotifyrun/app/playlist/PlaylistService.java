package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.AddMusicRecommendationsException.*;
import static com.ksaraev.spotifyrun.exception.business.CreateAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.GetAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.PlaylistExistenceException.*;
import static com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException.*;

import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.Runner;
import com.ksaraev.spotifyrun.exception.business.*;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.service.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService implements AppPlaylistService {

  private final PlaylistMapper mapper;
  private final PlaylistRepository repository;
  private final AppPlaylistConfig config;
  private final SpotifyPlaylistItemService spotifyPlaylistService;
  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;
  private final SpotifyUserProfileItemService spotifyUserProfileService;
  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Override
  public boolean isRelationExists(AppUser appUser) {
    try {
      String id = appUser.getId();
      return repository.existsByRunnerId(id);
    } catch (RuntimeException e) {
      throw new PlaylistExistenceException(UNABLE_TO_GET_PLAYLIST_STATUS + e.getMessage(), e);
    }
  }

  @Override
  public AppPlaylist getPlaylist(AppUser appUser) {
    Optional<Playlist> optionalPlaylist;
    try {
      String id = appUser.getId();
      optionalPlaylist = repository.findByRunnerId(id);
    } catch (RuntimeException e) {
      throw new GetAppPlaylistException(UNABLE_TO_GET_APP_PLAYLIST + e.getMessage(), e);
    }
    return optionalPlaylist.orElseThrow(
        () -> new GetAppPlaylistException(UNABLE_TO_GET_APP_PLAYLIST));
  }

  @Override
  public AppPlaylist createPlaylist(AppUser appUser) {
    try {
      SpotifyUserProfileItem userItem = spotifyUserProfileService.getCurrentUser();
      SpotifyPlaylistItemDetails playlistItemDetails = config.getDetails();
      SpotifyPlaylistItem playlistItem =
          spotifyPlaylistService.createPlaylist(userItem, playlistItemDetails);
      Playlist playlist = mapper.mapToEntity(playlistItem, (Runner) appUser);
      return repository.save(playlist);
    } catch (RuntimeException e) {
      throw new CreateAppPlaylistException(UNABLE_TO_CREATE_APP_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public void addMusic(AppPlaylist appPlaylist) {
    try {
      List<SpotifyTrackItem> topTracks = getTopTracks();
      List<SpotifyTrackItem> recommendations = getRecommendations(topTracks);

      String id = appPlaylist.getId();

      SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(id);

      List<SpotifyTrackItem> workoutTracks = playlistItem.getTracks();

      if (!workoutTracks.isEmpty()) {

        List<SpotifyTrackItem> tracksDiff =
            Stream.of(recommendations, workoutTracks)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(SpotifyTrackItem::getId, p -> p, (p, q) -> p))
                .values()
                .stream()
                .toList();

        List<SpotifyTrackItem> tracksToRemove =
            workoutTracks.stream()
                .filter(
                    workoutTrack ->
                        tracksDiff.stream()
                            .anyMatch(trackDiff -> trackDiff.getId().equals(workoutTrack.getId())))
                .toList();

        recommendations =
            recommendations.stream()
                .filter(
                    recommendation ->
                        tracksDiff.stream()
                            .anyMatch(
                                trackDiff -> trackDiff.getId().equals(recommendation.getId())))
                .toList();

        spotifyPlaylistService.removeTracks(id, tracksToRemove);
      }

      spotifyPlaylistService.addTracks(id, recommendations);
      playlistItem = spotifyPlaylistService.getPlaylist(id);
      Playlist playlist = mapper.updateEntity((Playlist) appPlaylist, playlistItem);
      repository.save(playlist);
    } catch (RuntimeException e) {
      throw new AddMusicRecommendationsException(
          UNABLE_TO_ADD_MUSIC_RECOMMENDATIONS + e.getMessage(), e);
    }
  }

  public void updateMusic(AppPlaylist appPlaylist) {}

  private List<SpotifyTrackItem> getTopTracks() {
    List<SpotifyTrackItem> topTracks = spotifyTopTracksService.getUserTopTracks();
    if (topTracks.isEmpty()) {
      throw new UserTopTracksNotFoundException(
          UserTopTracksNotFoundException.USER_TOP_TRACKS_NOT_FOUND);
    }
    return topTracks;
  }

  private List<SpotifyTrackItem> getRecommendations(List<SpotifyTrackItem> trackItems) {
    List<SpotifyTrackItem> musicRecommendations =
        trackItems.stream()
            .map(
                track ->
                    spotifyRecommendationsService.getRecommendations(
                        List.of(track), config.getMusicFeatures()))
            .flatMap(List::stream)
            .distinct()
            .limit(config.getSize())
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
    return musicRecommendations;
  }
}

package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.AddMusicRecommendationsException.*;
import static com.ksaraev.spotifyrun.exception.business.CreateAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.GetAppPlaylistException.*;
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
      spotifyPlaylistService.addTracks(id, recommendations);
      SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(id);
      Playlist playlist = mapper.updateEntity((Playlist) appPlaylist, playlistItem);
      repository.save(playlist);
    } catch (RuntimeException e) {
      throw new AddMusicRecommendationsException(
          UNABLE_TO_ADD_MUSIC_RECOMMENDATIONS + e.getMessage(), e);
    }
  }

  @Override
  public void updateMusic(AppPlaylist appPlaylist) {
    try {
      List<SpotifyTrackItem> topTracks = getTopTracks();
      List<SpotifyTrackItem> recommendations = getRecommendations(topTracks);
      String id = appPlaylist.getId();
      SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(id);
      List<SpotifyTrackItem> workoutTracks = playlistItem.getTracks();

      List<SpotifyTrackItem> tracksToAdd =
          recommendations.stream()
              .filter(
                  recommendation ->
                      workoutTracks.stream()
                          .noneMatch(
                              workoutTrack -> workoutTrack.getId().equals(recommendation.getId())))
              .toList();

      List<SpotifyTrackItem> tracksToRemove =
          workoutTracks.stream()
              .filter(
                  workoutTrack ->
                      recommendations.stream()
                          .noneMatch(
                              recommendation ->
                                  recommendation.getId().equals(workoutTrack.getId())))
              .toList();

      spotifyPlaylistService.removeTracks(id, tracksToRemove);
      spotifyPlaylistService.addTracks(id, tracksToAdd);
      playlistItem = spotifyPlaylistService.getPlaylist(id);
      Playlist playlist = mapper.updateEntity((Playlist) appPlaylist, playlistItem);
      repository.save(playlist);

    } catch (RuntimeException e) {
      throw new AddMusicRecommendationsException(
          UNABLE_TO_ADD_MUSIC_RECOMMENDATIONS + e.getMessage(), e);
    }
  }

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

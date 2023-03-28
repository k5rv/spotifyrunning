package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.AddMusicRecommendationsException.*;
import static com.ksaraev.spotifyrun.exception.business.CreateAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.GetAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException.*;

import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.AppUserService;
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
  private final PlaylistRepository playlistRepository;

  private final AppUserService userService;
  private final AppPlaylistConfig config;
  private final SpotifyPlaylistItemService spotifyPlaylistService;
  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;
  private final SpotifyUserProfileItemService spotifyUserProfileService;
  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Override
  public AppPlaylist createPlaylist() {
    SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
    String spotifyUserId = userProfileItem.getId();
    String spotifyUserName = userProfileItem.getName();

    AppUser appUser =
        userService.isUserExists(spotifyUserId)
            ? userService.getUser(spotifyUserId)
            : userService.createUser(spotifyUserId, spotifyUserName);

    boolean isUserPlaylistExists = userService.hasPlaylist(appUser);

    Playlist playlist;
    if (!isUserPlaylistExists) {
      SpotifyPlaylistItemDetails playlistItemDetails = config.getDetails();
      SpotifyPlaylistItem playlistItem =
          spotifyPlaylistService.createPlaylist(userProfileItem, playlistItemDetails);
      playlist = mapper.mapToEntity(playlistItem, (Runner) appUser);
      playlistRepository.save(playlist);
    } else {
      String id = appUser.getId();
      playlist =
          playlistRepository
              .findByRunnerId(id)
              .orElseThrow(() -> new CreateAppPlaylistException(UNABLE_TO_CREATE_APP_PLAYLIST));
    }

    String appPlaylistId = playlist.getId();
    SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
    List<SpotifyTrackItem> playlistTracks = playlistItem.getTracks();

    List<SpotifyTrackItem> topTracks = getUserTopTracks();
    List<SpotifyTrackItem> recommendations = getRecommendations(topTracks);

    List<SpotifyTrackItem> tracksUpdate =
        recommendations.stream()
            .filter(
                recommendation ->
                    playlistTracks.stream()
                        .noneMatch(
                            playlistTrackItem ->
                                playlistTrackItem.getId().equals(recommendation.getId())))
            .toList();

    if (tracksUpdate.isEmpty()) {
      log.warn("playlist include tracks already, no changes will be be applied");
      return playlist;
    }

    if (playlistTracks.isEmpty()) {
      spotifyPlaylistService.addTracks(appPlaylistId, tracksUpdate);
      playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
      playlist = mapper.updateEntity(playlist, playlistItem);
      return playlistRepository.save(playlist);
    }

    List<SpotifyTrackItem> tracksRemove =
        playlistTracks.stream()
            .filter(
                playlistTrackItem ->
                    recommendations.stream()
                        .noneMatch(
                            recommendation ->
                                recommendation.getId().equals(playlistTrackItem.getId())))
            .toList();

    if (tracksRemove.isEmpty()) {
      spotifyPlaylistService.addTracks(appPlaylistId, tracksUpdate);
      playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
      playlist = mapper.updateEntity(playlist, playlistItem);
      return playlistRepository.save(playlist);
    }

    spotifyPlaylistService.removeTracks(appPlaylistId, tracksRemove);
    spotifyPlaylistService.addTracks(appPlaylistId, tracksUpdate);
    playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
    playlist = mapper.updateEntity(playlist, playlistItem);
    return playlistRepository.save(playlist);
  }

  private List<SpotifyTrackItem> getUserTopTracks() {
    return spotifyTopTracksService.getUserTopTracks();
  }

  private List<SpotifyTrackItem> getRecommendations(List<SpotifyTrackItem> trackItems) {
    return trackItems.stream()
        .map(
            track ->
                spotifyRecommendationsService.getRecommendations(
                    List.of(track), config.getMusicFeatures()))
        .flatMap(List::stream)
        .sorted(Comparator.comparingInt(SpotifyTrackItem::getPopularity).reversed())
        .distinct()
        .limit(config.getSize())
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                  Collections.shuffle(list);
                  return list;
                }));
  }
}

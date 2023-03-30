package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.CreateAppPlaylistException.*;

import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.user.*;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistService implements AppPlaylistService {

  private final AppUserMapper appUserMapper;

  private final AppUserService userService;

  private final AppPlaylistMapper playlistMapper;

  private final AppPlaylistConfig playlistConfig;

  private final PlaylistRepository playlistRepository;

  private final SpotifyPlaylistItemService spotifyPlaylistService;

  private final SpotifyUserProfileItemService spotifyUserProfileService;

  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;

  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  public AppPlaylist createPlaylist(AppUser appUser) {
    SpotifyUserProfileItem userProfileItem = appUserMapper.mapToDto(appUser);
    SpotifyPlaylistItemDetails playlistItemDetails = playlistConfig.getDetails();
    SpotifyPlaylistItem playlistItem =
        spotifyPlaylistService.createPlaylist(userProfileItem, playlistItemDetails);
    AppPlaylist appPlaylist = playlistMapper.mapToEntity(playlistItem);
    return playlistRepository.save((Playlist) appPlaylist);
  }

  @Override
  public Optional<AppPlaylist> getPlaylist(AppUser appUser) {
    try {
      String userId = appUser.getId();
      Optional<Playlist> optionalPlaylist = playlistRepository.findByRunnerId(userId);
      if (optionalPlaylist.isEmpty()) return Optional.empty();

      AppPlaylist appPlaylist = optionalPlaylist.get();
      String playlistId = appPlaylist.getId();
      String playlistSnapshotId = appPlaylist.getSnapshotId();

      SpotifyUserProfileItem userProfileItem = appUserMapper.mapToDto(appUser);
      List<SpotifyPlaylistItem> playlistItems =
          spotifyPlaylistService.getUserPlaylists(userProfileItem);

      boolean isPlaylistFound =
          playlistItems.stream()
              .anyMatch(playlistItem -> playlistItem.getId().equals(playlistId));

      if (!isPlaylistFound) {
        playlistRepository.deleteById(playlistId);
        appPlaylist = createPlaylist(appUser);
        return Optional.of(appPlaylist);
      }

      boolean isSnapshotIdentical =
          playlistItems.stream()
              .anyMatch(playlistItem -> playlistItem.getSnapshotId().equals(playlistSnapshotId));

      if (!isSnapshotIdentical) {
        SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(playlistId);
        appPlaylist = playlistMapper.mapToEntity(playlistItem);
        Playlist playlist = playlistRepository.save((Playlist) appPlaylist);
        playlistRepository.deleteByIdAndSnapshotId(playlistId, playlistSnapshotId);
        return Optional.of(playlist);
      }
      return optionalPlaylist.map(AppPlaylist.class::cast);
    } catch (RuntimeException e) {
      throw new AppPlaylistSearchingException(appUser.getId(), e);
    }
  }

  public AppPlaylist updateTracks(AppPlaylist appPlaylist, List<AppTrack> appTracks) {
    return null;

    /*
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
      playlist = playlistMapper.updateEntity(playlist, playlistItem);
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
      playlist = playlistMapper.updateEntity(playlist, playlistItem);
      return playlistRepository.save(playlist);
    }

    spotifyPlaylistService.removeTracks(appPlaylistId, tracksRemove);
    spotifyPlaylistService.addTracks(appPlaylistId, tracksUpdate);
    playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
    playlist = playlistMapper.updateEntity(playlist, playlistItem);
    return playlistRepository.save(playlist);
     */
  }

  @Override
  public AppPlaylist createPlaylist() {
    SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUser();
    AppUser appUser = appUserMapper.mapToEntity(userProfileItem);
    String appUserId = appUser.getId();
    String appUserName = appUser.getName();

    appUser =
        userService.isUserRegistered(appUserId)
            ? userService
                .getUser(appUserId)
                .orElseThrow(() -> new AppUserSearchingException(appUserId))
            : userService.registerUser(appUserId, appUserName);

    boolean isEmpty = Optional.ofNullable(appUser.getPlaylists()).isEmpty();

    AppPlaylist appPlaylist;
    if (!isEmpty) {
      SpotifyPlaylistItemDetails playlistItemDetails = playlistConfig.getDetails();
      SpotifyPlaylistItem playlistItem =
          spotifyPlaylistService.createPlaylist(userProfileItem, playlistItemDetails);
      appPlaylist = playlistMapper.mapToEntity(playlistItem);
      playlistRepository.save((Playlist) appPlaylist);
    } else {
      String id = appUser.getId();
      appPlaylist =
          playlistRepository
              .findByRunnerId(id)
              .orElseThrow(() -> new CreateAppPlaylistException(UNABLE_TO_CREATE_APP_PLAYLIST));
    }

    String appPlaylistId = appPlaylist.getId();
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
      return appPlaylist;
    }

    if (playlistTracks.isEmpty()) {
      spotifyPlaylistService.addTracks(appPlaylistId, tracksUpdate);
      playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
      appPlaylist = playlistMapper.mapToEntity(playlistItem);
      return playlistRepository.save((Playlist) appPlaylist);
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
      appPlaylist = playlistMapper.mapToEntity(playlistItem);
      return playlistRepository.save((Playlist) appPlaylist);
    }

    spotifyPlaylistService.removeTracks(appPlaylistId, tracksRemove);
    spotifyPlaylistService.addTracks(appPlaylistId, tracksUpdate);
    playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
    appPlaylist = playlistMapper.mapToEntity(playlistItem);
    return playlistRepository.save((Playlist) appPlaylist);
  }

  private List<SpotifyTrackItem> getUserTopTracks() {
    return spotifyTopTracksService.getUserTopTracks();
  }

  private List<SpotifyTrackItem> getRecommendations(List<SpotifyTrackItem> trackItems) {
    return trackItems.stream()
        .map(
            track ->
                spotifyRecommendationsService.getRecommendations(
                    List.of(track), playlistConfig.getMusicFeatures()))
        .flatMap(List::stream)
        .sorted(Comparator.comparingInt(SpotifyTrackItem::getPopularity).reversed())
        .distinct()
        .limit(playlistConfig.getSize())
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                  Collections.shuffle(list);
                  return list;
                }));
  }
}

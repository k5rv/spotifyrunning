package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.AppTrackMapper;
import com.ksaraev.spotifyrun.app.user.*;
import com.ksaraev.spotifyrun.spotify.model.SpotifyItem;
import com.ksaraev.spotifyrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.util.*;
import java.util.stream.Stream;
import com.ksaraev.spotifyrun.spotify.service.SpotifyPlaylistItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistService implements AppPlaylistService {

  private final AppTrackMapper appTrackMapper;

  private final AppUserMapper appUserMapper;

  private final AppPlaylistMapper playlistMapper;

  private final AppPlaylistConfig playlistConfig;

  private final PlaylistRepository playlistRepository;

  private final SpotifyPlaylistItemService spotifyPlaylistService;

  @Override
  public AppPlaylist createPlaylist(AppUser appUser) {
    try {
      String appUserId = appUser.getId();
      log.info("Creating playlist for user with id [" + appUser.getId() + "]");
      SpotifyUserProfileItem spotifyUser = appUserMapper.mapToDto(appUser);
      SpotifyPlaylistItemDetails spotifyPlaylistDetails = playlistConfig.getDetails();
      SpotifyPlaylistItem spotifyPlaylist =
          spotifyPlaylistService.createPlaylist(spotifyUser, spotifyPlaylistDetails);
      String spotifyPlaylistId = spotifyPlaylist.getId();
      spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      AppPlaylist appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist = playlistRepository.save((Playlist) appPlaylist);
      String appPlaylistId = appPlaylist.getId();
      log.info(
          "Created playlist with id ["
              + appPlaylistId
              + "] for user with id ["
              + appUserId
              + "], saved in app");
      return appPlaylist;
    } catch (RuntimeException e) {
      throw new AppPlaylistCreatingException(appUser.getId(), e);
    }
  }

  @Override
  public Optional<AppPlaylist> getPlaylist(AppUser appUser) {
    try {
      String appUserId = appUser.getId();
      log.info("Getting playlist for user with id [" + appUserId + "]");
      Optional<Playlist> appRunningWorkoutPlaylist = playlistRepository.findByRunnerId(appUserId);
      boolean appPlaylistExists = appRunningWorkoutPlaylist.isPresent();
      if (!appPlaylistExists) {
        log.info(
            "User with id ["
                + appUserId
                + "] doesn't have any playlists saved in app, returning empty result");
        return Optional.empty();
      }

      SpotifyUserProfileItem spotifyUser = appUserMapper.mapToDto(appUser);
      List<SpotifyPlaylistItem> spotifyUserPlaylists =
          spotifyPlaylistService.getUserPlaylists(spotifyUser);
      AppPlaylist appPlaylist = appRunningWorkoutPlaylist.get();
      String appPlaylistId = appPlaylist.getId();
      Optional<SpotifyPlaylistItem> spotifyRunningWorkoutPlaylist =
          spotifyUserPlaylists.stream()
              .filter(spotifyPlaylist -> spotifyPlaylist.getId().equals(appPlaylistId))
              .findFirst();
      boolean spotifyPlaylistExists = spotifyRunningWorkoutPlaylist.isPresent();
      if (!spotifyPlaylistExists) {
        log.info(
            "User with id ["
                + appUserId
                + "] doesn't have any running workout playlists saved in spotify that correspond to app playlist with id ["
                + appPlaylist.getId()
                + "], deleting saved in app playlist, returning empty result");
        appUser.removePlaylist(appPlaylist);
        playlistRepository.deleteById(appPlaylistId);
        return Optional.empty();
      }

      String spotifyPlaylistId =
          spotifyRunningWorkoutPlaylist.map(SpotifyItem::getId).orElseThrow();
      SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      String spotifyPlaylistSnapshotId = spotifyPlaylist.getSnapshotId();
      String appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      boolean snapshotIdentical = spotifyPlaylistSnapshotId.equals(appPlaylistSnapshotId);
      if (snapshotIdentical) {
        log.info(
            "User with id ["
                + appUserId
                + "] has running workout playlists, both saved in app and spotify with the exact same snapshot id ["
                + appPlaylistSnapshotId
                + "] returning playlist with id ["
                + appPlaylistId
                + "]");
        return Optional.of(appPlaylist);
      }

      log.info(
          "User with id ["
              + appUserId
              + "] has running workout playlists, both saved in app and spotify with different snapshot ids: ["
              + appPlaylistSnapshotId
              + "] and ["
              + spotifyPlaylistSnapshotId
              + "] respectively. Updating saved in app playlist from spotify.");
      List<AppTrack> customTracks = reviseCustomTracks(appPlaylist, spotifyPlaylist);
      List<AppTrack> rejectedTracks = reviseRejectedTracks(appPlaylist, spotifyPlaylist);
      appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist.setCustomTracks(customTracks);
      appPlaylist.setRejectedTracks(rejectedTracks);
      appPlaylist = playlistRepository.save((Playlist) appPlaylist);
      appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      log.info(
          "Updated playlist with id ["
              + appPlaylist.getId()
              + "] and snapshotId ["
              + appPlaylistSnapshotId
              + "] for user ["
              + appUserId
              + "] from spotify");
      return Optional.of(appPlaylist);
    } catch (RuntimeException e) {
      throw new AppPlaylistGettingException(appUser.getId(), e);
    }
  }

  @Override
  public AppPlaylist addTracks(AppPlaylist appPlaylist, List<AppTrack> appTracks) {
    try {
      String appPlaylistId = appPlaylist.getId();
      int appTracksNumber = appTracks.size();
      log.info(
          "Adding [" + appTracksNumber + "] tracks to playlist with id [" + appPlaylistId + "]");
      Optional<Playlist> appRunningWorkoutPlaylist = playlistRepository.findById(appPlaylistId);
      if (appRunningWorkoutPlaylist.isEmpty()) {
        log.error("Playlist with id [" + appPlaylistId + "] doesn't exist in app");
        throw new AppPlaylistDoesNotExistException(appPlaylistId);
      }

      AppUser appUser = appPlaylist.getOwner();
      SpotifyUserProfileItem spotifyUser = appUserMapper.mapToDto(appUser);
      List<SpotifyPlaylistItem> spotifyUserPlaylists =
          spotifyPlaylistService.getUserPlaylists(spotifyUser);
      Optional<SpotifyPlaylistItem> spotifyRunningWorkoutPlaylist =
          spotifyUserPlaylists.stream()
              .filter(playlist -> playlist.getId().equals(appPlaylistId))
              .findFirst();

      boolean spotifyPlaylistExists = spotifyRunningWorkoutPlaylist.isPresent();

      if (!spotifyPlaylistExists) {
        log.info(
            "Playlist saved in app with id ["
                + appPlaylistId
                + "] doesn't correspond to any playlist in spotify, deleting saved in app playlist and creating new one");
        appUser.removePlaylist(appPlaylist);
        playlistRepository.deleteById(appPlaylistId);
        appPlaylist = createPlaylist(appUser);
        List<SpotifyTrackItem> spotifyAddTracks =
            appTracks.stream().map(appTrackMapper::mapToDto).toList();
        spotifyPlaylistService.addTracks(appPlaylist.getId(), spotifyAddTracks);
        SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(appPlaylistId);
        appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
        appPlaylist = playlistRepository.save((Playlist) appPlaylist);
        String appPlaylistSnapshotId = appPlaylist.getSnapshotId();
        log.info(
            "Added ["
                + appTracksNumber
                + "] tracks to playlist with id ["
                + appPlaylistId
                + "] and snapshot id ["
                + appPlaylistSnapshotId
                + "] saved in app");
        return appPlaylist;
      }

      String spotifyPlaylistId =
          spotifyRunningWorkoutPlaylist.map(SpotifyItem::getId).orElseThrow();
      SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      List<SpotifyTrackItem> spotifyPlaylistTracks = spotifyPlaylist.getTracks().stream().toList();

      List<AppTrack> rejectedTracks = appPlaylist.getRejectedTracks();
      int rejectedTracksSize = rejectedTracks.size();
      if (rejectedTracksSize > 0)
        log.info(
            "Considering ["
                + rejectedTracksSize
                + "] tracks previously removed manually from spotify playlist while adding tracks to playlist with id ["
                + appPlaylistId
                + "]");

      List<SpotifyTrackItem> spotifyAddTracks =
          appTracks.stream()
              .filter(Objects::nonNull)
              .filter(
                  appTrack ->
                      spotifyPlaylistTracks.stream()
                          .noneMatch(
                              spotifyPlaylistTrack ->
                                  spotifyPlaylistTrack.getId().equals(appTrack.getId())))
              .filter(
                  appTrack ->
                      rejectedTracks.stream()
                          .noneMatch(
                              rejectedTrack -> rejectedTrack.getId().equals(appTrack.getId())))
              .map(appTrackMapper::mapToDto)
              .toList();

      List<AppTrack> customTracks = appPlaylist.getCustomTracks();
      int customTracksSize = customTracks.size();
      if (customTracksSize > 0)
        log.info(
            "Considering ["
                + customTracksSize
                + "] tracks previously added manually to spotify playlist while adding tracks to playlist with id ["
                + appPlaylistId
                + "]");

      List<SpotifyTrackItem> spotifyRemoveTracks =
          spotifyPlaylistTracks.stream()
              .filter(Objects::nonNull)
              .filter(
                  spotifyPlaylistTrack ->
                      appTracks.stream()
                          .noneMatch(
                              appTrack -> appTrack.getId().equals(spotifyPlaylistTrack.getId())))
              .filter(
                  appTrack ->
                      customTracks.stream()
                          .noneMatch(customTrack -> customTrack.getId().equals(appTrack.getId())))
              .toList();

      boolean spotifyRemoveTracksExist = !spotifyRemoveTracks.isEmpty();
      if (spotifyRemoveTracksExist) {
        spotifyPlaylistService.removeTracks(spotifyPlaylistId, spotifyRemoveTracks);
      }

      boolean spotifyAddTracksExist = !spotifyAddTracks.isEmpty();
      if (spotifyAddTracksExist) {
        spotifyPlaylistService.addTracks(spotifyPlaylistId, spotifyAddTracks);
      }

      spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist.setCustomTracks(customTracks);
      appPlaylist.setRejectedTracks(rejectedTracks);
      appPlaylist = playlistRepository.save((Playlist) appPlaylist);
      int addTracksNumber = spotifyAddTracks.size();
      int removeTracksNumber = spotifyRemoveTracks.size();
      log.info(
          "Added ["
              + addTracksNumber
              + "] and removed ["
              + removeTracksNumber
              + "] tracks to playlist with id ["
              + appPlaylist.getId()
              + "], snapshotId ["
              + appPlaylist.getSnapshotId()
              + "], saved in app");
      return appPlaylist;
    } catch (RuntimeException e) {
      throw new AppPlaylistAddTracksException(appPlaylist.getId(), e);
    }
  }

  private List<AppTrack> reviseCustomTracks(
      AppPlaylist appPlaylist, SpotifyPlaylistItem spotifyPlaylist) {

    List<AppTrack> targetTracks = appPlaylist.getTracks();
    List<AppTrack> customTracks = appPlaylist.getCustomTracks();
    List<SpotifyTrackItem> sourceTracks = spotifyPlaylist.getTracks();

    List<AppTrack> tracksInclusion =
        sourceTracks.stream()
            .filter(
                sourceTrack ->
                    targetTracks.stream()
                        .noneMatch(targetTrack -> targetTrack.getId().equals(sourceTrack.getId())))
            .filter(
                track ->
                    customTracks.stream()
                        .noneMatch(favoriteTrack -> favoriteTrack.getId().equals(track.getId())))
            .map(appTrackMapper::mapToEntity)
            .toList();

    List<AppTrack> tracksExclusion =
        customTracks.stream()
            .filter(
                favoriteTrack ->
                    sourceTracks.stream()
                        .noneMatch(
                            sourceTrack -> sourceTrack.getId().equals(favoriteTrack.getId())))
            .toList();

    return Stream.of(customTracks, tracksInclusion)
        .flatMap(Collection::stream)
        .filter(
            track ->
                tracksExclusion.stream()
                    .noneMatch(removeTrack -> removeTrack.getId().equals(track.getId())))
        .toList();
  }

  private List<AppTrack> reviseRejectedTracks(
      AppPlaylist appPlaylist, SpotifyPlaylistItem spotifyPlaylist) {

    List<AppTrack> targetTracks = appPlaylist.getTracks();
    List<AppTrack> rejectedTracks = appPlaylist.getRejectedTracks();
    List<SpotifyTrackItem> sourceTracks = spotifyPlaylist.getTracks();

    List<AppTrack> tracksInclusion =
        targetTracks.stream()
            .filter(
                targetTrack ->
                    sourceTracks.stream()
                        .noneMatch(sourceTrack -> sourceTrack.getId().equals(targetTrack.getId())))
            .filter(
                track ->
                    rejectedTracks.stream()
                        .noneMatch(rejectedTrack -> rejectedTrack.getId().equals(track.getId())))
            .toList();

    List<AppTrack> tracksExclusion =
        rejectedTracks.stream()
            .filter(
                rejectedTrack ->
                    sourceTracks.stream()
                        .anyMatch(sourceTrack -> sourceTrack.getId().equals(rejectedTrack.getId())))
            .toList();

    return Stream.of(rejectedTracks, tracksInclusion)
        .flatMap(Collection::stream)
        .filter(
            track ->
                tracksExclusion.stream()
                    .noneMatch(removeTrack -> removeTrack.getId().equals(track.getId())))
        .toList();
  }
}

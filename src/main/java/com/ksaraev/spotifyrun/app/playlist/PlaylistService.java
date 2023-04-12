package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.exception.AppAuthenticationException;
import com.ksaraev.spotifyrun.app.exception.AppSpotifyServiceInteractionException;
import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.AppTrackMapper;
import com.ksaraev.spotifyrun.app.user.*;
import com.ksaraev.spotifyrun.spotify.exception.*;
import com.ksaraev.spotifyrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.spotify.service.SpotifyPlaylistItemService;
import java.util.*;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistService implements AppPlaylistService {

  public static final String PLAYLIST_WITH_ID = "Playlist with id";

  private final PlaylistRepository playlistRepository;

  private final SpotifyPlaylistItemService spotifyPlaylistService;

  private final AppPlaylistConfig playlistConfig;

  private final AppPlaylistMapper playlistMapper;

  private final AppTrackMapper appTrackMapper;

  private final AppUserMapper appUserMapper;

  @Override
  public AppPlaylist createPlaylist(AppUser appUser) {
    try {
      String appUserId = appUser.getId();
      log.info("Creating playlist for user id with [" + appUserId + "]");
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
          "Created playlist with id [" + appPlaylistId + "] for user with id [" + appUserId + "]");
      return appPlaylist;
    } catch (SpotifyServiceAuthenticationException e) {
      throw new AppAuthenticationException(e);
    } catch (SpotifyPlaylistServiceException e) {
      throw new AppSpotifyServiceInteractionException(e);
    } catch (RuntimeException e) {
      throw new AppPlaylistServiceCreatePlaylistException(e);
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
                + "] doesn't have any playlists in app. Returning empty result.");
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
            PLAYLIST_WITH_ID
                + " ["
                + appPlaylistId
                + "] not found in Spotify. Deleting app playlist and returning empty result.");
        appUser.removePlaylist(appPlaylist);
        playlistRepository.deleteById(appPlaylistId);
        return Optional.empty();
      }

      String spotifyPlaylistId =
          spotifyRunningWorkoutPlaylist.map(SpotifyPlaylistItem::getId).orElseThrow();
      SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      String spotifyPlaylistSnapshotId = spotifyPlaylist.getSnapshotId();
      String appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      boolean snapshotIdentical = spotifyPlaylistSnapshotId.equals(appPlaylistSnapshotId);
      if (snapshotIdentical) {
        log.info(
            PLAYLIST_WITH_ID
                + " ["
                + appPlaylistId
                + "] has the exact same snapshot version in app and Spotify. Returning playlist with snapshot id ["
                + appPlaylistSnapshotId
                + "].");
        return Optional.of(appPlaylist);
      }
      log.info(
          PLAYLIST_WITH_ID
              + " ["
              + appPlaylistId
              + "] found in app and Spotify with the different snapshot versions ["
              + appPlaylistSnapshotId
              + "] and ["
              + spotifyPlaylistSnapshotId
              + "] respectively. Updating app version from Spotify.");
      List<AppTrack> customTracks = reviseCustomTracks(appPlaylist, spotifyPlaylist);
      List<AppTrack> rejectedTracks = reviseRejectedTracks(appPlaylist, spotifyPlaylist);
      appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist.setCustomTracks(customTracks);
      appPlaylist.setRejectedTracks(rejectedTracks);
      appPlaylist = playlistRepository.save((Playlist) appPlaylist);
      appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      log.info(
          "Updated playlist with id ["
              + appPlaylistId
              + "] and snapshot id ["
              + appPlaylistSnapshotId
              + "] from Spotify. Returning playlist.");
      return Optional.of(appPlaylist);
    } catch (SpotifyServiceAuthenticationException e) {
      throw new AppAuthenticationException(e);
    } catch (SpotifyPlaylistServiceException e) {
      throw new AppSpotifyServiceInteractionException(e);
    } catch (RuntimeException e) {
      throw new AppPlaylistServiceGetPlaylistException(e);
    }
  }

  @Override
  public AppPlaylist addTracks(AppPlaylist appPlaylist, List<AppTrack> appTracks) {
    try {
      String appPlaylistId = appPlaylist.getId();
      int appTracksNumber = appTracks.size();
      log.info(
          "Adding ["
              + appTracksNumber
              + "] tracks to "
              + PLAYLIST_WITH_ID
              + " ["
              + appPlaylistId
              + "]");
      Optional<Playlist> appRunningWorkoutPlaylist = playlistRepository.findById(appPlaylistId);
      if (appRunningWorkoutPlaylist.isEmpty()) {
        log.error(PLAYLIST_WITH_ID + appPlaylistId + "] doesn't exist in app");
        throw new AppPlaylistServicePlaylistDoesNotExistException(appPlaylistId);
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
            PLAYLIST_WITH_ID
                + " ["
                + appPlaylistId
                + "] not found in Spotify. Deleting saved in app playlist and creating new one.");
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
                + PLAYLIST_WITH_ID
                + " ["
                + appPlaylistId
                + "] and snapshot id ["
                + appPlaylistSnapshotId
                + "]. Saved in app.");
        return appPlaylist;
      }

      String spotifyPlaylistId =
          spotifyRunningWorkoutPlaylist.map(SpotifyPlaylistItem::getId).orElseThrow();
      SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      List<SpotifyTrackItem> spotifyPlaylistTracks = spotifyPlaylist.getTracks().stream().toList();

      List<AppTrack> rejectedTracks = appPlaylist.getRejectedTracks();
      int rejectedTracksSize = rejectedTracks.size();
      if (rejectedTracksSize > 0)
        log.info(
            "Determining ["
                + rejectedTracksSize
                + "] tracks previously removed outside of the app from Spotify playlist with id ["
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
            "Determining ["
                + customTracksSize
                + "] tracks previously added outside of the app to Spotify playlist with id ["
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
              + removeTracksNumber + "] tracks for playlist with id ["
              + appPlaylistId
              + "] and  snapshotId ["
              + appPlaylist.getSnapshotId()
              + "]. Saved in app.");
      return appPlaylist;
    } catch (SpotifyServiceAuthenticationException e) {
      throw new AppAuthenticationException(e);
    } catch (SpotifyPlaylistServiceException e) {
      throw new AppSpotifyServiceInteractionException(e);
    } catch (RuntimeException e) {
      throw new AppPlaylistServiceAddTracksException(e);
    }
  }

  private List<AppTrack> reviseCustomTracks(
      AppPlaylist appPlaylist, SpotifyPlaylistItem spotifyPlaylist) {
    try {
      List<AppTrack> targetTracks = appPlaylist.getTracks();
      List<AppTrack> customTracks = appPlaylist.getCustomTracks();
      List<SpotifyTrackItem> sourceTracks = spotifyPlaylist.getTracks();

      List<AppTrack> tracksInclusion =
          sourceTracks.stream()
              .filter(
                  sourceTrack ->
                      targetTracks.stream()
                          .noneMatch(
                              targetTrack -> targetTrack.getId().equals(sourceTrack.getId())))
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
    } catch (RuntimeException e) {
      throw new AppPlaylistServiceTracksRevisionException(appPlaylist.getId(), e);
    }
  }

  private List<AppTrack> reviseRejectedTracks(
      AppPlaylist appPlaylist, SpotifyPlaylistItem spotifyPlaylist) {
    try {
      List<AppTrack> targetTracks = appPlaylist.getTracks();
      List<AppTrack> rejectedTracks = appPlaylist.getRejectedTracks();
      List<SpotifyTrackItem> sourceTracks = spotifyPlaylist.getTracks();

      List<AppTrack> tracksInclusion =
          targetTracks.stream()
              .filter(
                  targetTrack ->
                      sourceTracks.stream()
                          .noneMatch(
                              sourceTrack -> sourceTrack.getId().equals(targetTrack.getId())))
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
                          .anyMatch(
                              sourceTrack -> sourceTrack.getId().equals(rejectedTrack.getId())))
              .toList();

      return Stream.of(rejectedTracks, tracksInclusion)
          .flatMap(Collection::stream)
          .filter(
              track ->
                  tracksExclusion.stream()
                      .noneMatch(removeTrack -> removeTrack.getId().equals(track.getId())))
          .toList();
    } catch (RuntimeException e) {
      throw new AppPlaylistServiceTracksRevisionException(appPlaylist.getId(), e);
    }
  }
}

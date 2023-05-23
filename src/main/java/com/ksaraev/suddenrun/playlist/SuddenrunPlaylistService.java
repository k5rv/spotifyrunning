package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.AppUserMapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunPlaylistService implements AppPlaylistService {

  private static final String PLAYLIST_WITH_ID = "Playlist with id";

  private static final String AND_SNAPSHOT_ID = "] and snapshot id [";

  private final SuddenrunPlaylistRepository repository;

  private final AppPlaylistRevisionService playlistRevisionService;

  private final SpotifyPlaylistItemService spotifyPlaylistService;

  private final SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  private final AppPlaylistMapper playlistMapper;

  private final AppUserMapper userMapper;

  private final AppTrackMapper trackMapper;

  @Override
  public AppPlaylist createPlaylist(@NotNull AppUser appUser) {
    String appUserId = appUser.getId();
    try {
      log.info("Creating playlist for user id with [" + appUserId + "]");
      SpotifyUserProfileItem spotifyUserProfile = userMapper.mapToItem(appUser);
      SpotifyPlaylistItemDetails spotifyPlaylistDetails = spotifyPlaylistConfig.getDetails();
      SpotifyPlaylistItem spotifyPlaylist =
          spotifyPlaylistService.createPlaylist(spotifyUserProfile, spotifyPlaylistDetails);
      String spotifyPlaylistId = spotifyPlaylist.getId();
      String spotifySnapshotId = spotifyPlaylist.getSnapshotId();
      log.info(
          "Created "
              + PLAYLIST_WITH_ID
              + " ["
              + spotifyPlaylistId
              + AND_SNAPSHOT_ID
              + spotifySnapshotId
              + "]");
      spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      spotifyPlaylistId = spotifyPlaylist.getId();
      spotifySnapshotId = spotifyPlaylist.getSnapshotId();
      log.info(
          "Get "
              + PLAYLIST_WITH_ID
              + " ["
              + spotifyPlaylistId
              + AND_SNAPSHOT_ID
              + spotifySnapshotId
              + "]");
      AppPlaylist appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
      String appPlaylistId = appPlaylist.getId();
      String appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      log.info(
          "Created playlist with id ["
              + appPlaylistId
              + AND_SNAPSHOT_ID
              + appPlaylistSnapshotId
              + "] "
              + "for user with id ["
              + appUserId
              + "]");
      return appPlaylist;
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new CreateSuddenrunPlaylistException(appUserId, e);
    }
  }

  @Override
  public Optional<AppPlaylist> getPlaylist(@NotNull AppUser appUser) {
    String userId = appUser.getId();
    try {
      log.info("Getting playlist for user with id [" + userId + "]");
      Optional<SuddenrunPlaylist> suddenrunPlaylist = repository.findByOwnerId(userId);
      boolean suddenrunPlaylistExists = suddenrunPlaylist.isPresent();
      if (!suddenrunPlaylistExists) {
        log.info(
            "User with id ["
                + userId
                + "] doesn't have any playlists in app. Returning empty result.");
        return Optional.empty();
      }

      SpotifyUserProfileItem spotifyUserProfile = userMapper.mapToItem(appUser);
      List<SpotifyPlaylistItem> spotifyUserPlaylists =
          spotifyPlaylistService.getUserPlaylists(spotifyUserProfile);
      AppPlaylist appPlaylist = suddenrunPlaylist.get();
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
        repository.deleteById(appPlaylistId);
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

      List<AppTrack> customTracks =
          playlistRevisionService.getAddedSourceTracks(appPlaylist, spotifyPlaylist);

      List<AppTrack> rejectedTracks =
          playlistRevisionService.getRemovedSourceTracks(appPlaylist, spotifyPlaylist);

      appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist.setCustomTracks(customTracks);
      appPlaylist.setRejectedTracks(rejectedTracks);
      appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
      appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      log.info(
          "Updated playlist with id ["
              + appPlaylistId
              + AND_SNAPSHOT_ID
              + appPlaylistSnapshotId
              + "] from Spotify. Returning playlist.");
      return Optional.of(appPlaylist);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new GetSuddenrunPlaylistException(userId, e);
    }
  }

  @Override
  public AppPlaylist addTracks(@NotNull AppPlaylist appPlaylist, List<AppTrack> appTracks) {
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

      Optional<SuddenrunPlaylist> appRunningWorkoutPlaylist = repository.findById(appPlaylistId);

      if (appRunningWorkoutPlaylist.isEmpty()) {
        log.error(PLAYLIST_WITH_ID + appPlaylistId + "] doesn't exist in app");
        throw new AppPlaylistServicePlaylistDoesNotExistException(appPlaylistId);
      }

      AppUser appUser = appPlaylist.getOwner();
      SpotifyUserProfileItem spotifyUser = userMapper.mapToItem(appUser);
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
        repository.deleteById(appPlaylistId);
        appPlaylist = createPlaylist(appUser);
        List<SpotifyTrackItem> spotifyAddTracks =
            appTracks.stream().map(trackMapper::mapToDto).toList();
        spotifyPlaylistService.addTracks(appPlaylist.getId(), spotifyAddTracks);
        SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(appPlaylistId);
        appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
        appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
        String appPlaylistSnapshotId = appPlaylist.getSnapshotId();
        log.info(
            "Added ["
                + appTracksNumber
                + PLAYLIST_WITH_ID
                + " ["
                + appPlaylistId
                + AND_SNAPSHOT_ID
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
          playlistRevisionService.getSourceTracksToAdd(
              appTracks, spotifyPlaylistTracks, rejectedTracks);

      List<AppTrack> customTracks = appPlaylist.getCustomTracks();
      int customTracksSize = customTracks.size();
      if (customTracksSize > 0) {
        log.info(
            "Determining ["
                + customTracksSize
                + "] tracks previously added outside of the app to Spotify playlist with id ["
                + appPlaylistId
                + "]");
      }

      List<SpotifyTrackItem> spotifyRemoveTracks =
          playlistRevisionService.getSourceTracksToRemove(
              appTracks, spotifyPlaylistTracks, customTracks);

      boolean spotifyRemoveTracksExist = !spotifyRemoveTracks.isEmpty();
      if (spotifyRemoveTracksExist) {
        String snapshotId =
            spotifyPlaylistService.removeTracks(spotifyPlaylistId, spotifyRemoveTracks);
        log.info(
            "Removed tracks from "
                + PLAYLIST_WITH_ID
                + " ["
                + spotifyPlaylistId
                + AND_SNAPSHOT_ID
                + snapshotId
                + "]");
      }

      boolean spotifyAddTracksExist = !spotifyAddTracks.isEmpty();
      if (spotifyAddTracksExist) {
        String snapshotId = spotifyPlaylistService.addTracks(spotifyPlaylistId, spotifyAddTracks);
        log.info(
            "Added tracks to "
                + PLAYLIST_WITH_ID
                + " ["
                + spotifyPlaylistId
                + AND_SNAPSHOT_ID
                + snapshotId
                + "]");
      }

      spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      String snapshotId = spotifyPlaylist.getSnapshotId();
      log.info(
          "Received "
              + PLAYLIST_WITH_ID
              + " ["
              + spotifyPlaylistId
              + AND_SNAPSHOT_ID
              + snapshotId
              + "]");
      appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist.setCustomTracks(customTracks);
      appPlaylist.setRejectedTracks(rejectedTracks);
      appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
      int addTracksNumber = spotifyAddTracks.size();
      int removeTracksNumber = spotifyRemoveTracks.size();
      log.info(
          "Added ["
              + addTracksNumber
              + "] and removed ["
              + removeTracksNumber
              + "] tracks for playlist with id ["
              + appPlaylistId
              + AND_SNAPSHOT_ID
              + appPlaylist.getSnapshotId()
              + "]. Saved in app.");
      return appPlaylist;
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new AppPlaylistServiceAddTracksException(e);
    }
  }
}

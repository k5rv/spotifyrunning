package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.AppTrackMapper;
import com.ksaraev.spotifyrun.app.user.*;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.service.*;
import java.util.*;
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
      String userId = appUser.getId();
      Optional<Playlist> optionalPlaylist = playlistRepository.findByRunnerId(userId);

      boolean isEmpty = optionalPlaylist.isEmpty();

      if (isEmpty) {
        SpotifyUserProfileItem userProfileItem = appUserMapper.mapToDto(appUser);
        SpotifyPlaylistItemDetails playlistItemDetails = playlistConfig.getDetails();
        SpotifyPlaylistItem playlistItem =
            spotifyPlaylistService.createPlaylist(userProfileItem, playlistItemDetails);
        String playlistId = playlistItem.getId();
        playlistItem = spotifyPlaylistService.getPlaylist(playlistId);
        AppPlaylist appPlaylist = playlistMapper.mapToEntity(playlistItem);
        return playlistRepository.save((Playlist) appPlaylist);
      }

      AppPlaylist appPlaylist = optionalPlaylist.get();
      String appPlaylistId = appPlaylist.getId();
      SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);

      boolean isIdentical = appPlaylist.getSnapshotId().equals(playlistItem.getSnapshotId());

      if (isIdentical) {
        return appPlaylist;
      }

      appUser.removePlaylist(appPlaylist);
      playlistRepository.deleteById(appPlaylistId);
      appPlaylist = playlistMapper.mapToEntity(playlistItem);
      return playlistRepository.save((Playlist) appPlaylist);
    } catch (RuntimeException e) {
      throw new AppPlaylistCreatingException(appUser.getId(), e);
    }
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

      boolean isFound =
          playlistItems.stream().anyMatch(playlistItem -> playlistItem.getId().equals(playlistId));

      if (!isFound) {
        appUser.removePlaylist(appPlaylist);
        playlistRepository.deleteById(playlistId);
        appPlaylist = createPlaylist(appUser);
        return Optional.of(appPlaylist);
      }

      boolean isIdentical =
          playlistItems.stream()
              .anyMatch(playlistItem -> playlistItem.getSnapshotId().equals(playlistSnapshotId));

      if (!isIdentical) {
        SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(playlistId);
        appUser.removePlaylist(appPlaylist);
        appPlaylist = playlistMapper.mapToEntity(playlistItem);
        playlistRepository.deleteByIdAndSnapshotId(playlistId, playlistSnapshotId);
        Playlist playlist = playlistRepository.save((Playlist) appPlaylist);
        return Optional.of(playlist);
      }
      return optionalPlaylist.map(AppPlaylist.class::cast);
    } catch (RuntimeException e) {
      throw new AppPlaylistSearchingException(appUser.getId(), e);
    }
  }

  @Override
  public AppPlaylist addTracks(AppPlaylist appPlaylist, List<AppTrack> appTracks) {
    try {
      AppUser appUser = appPlaylist.getOwner();
      SpotifyUserProfileItem spotifyUser = appUserMapper.mapToDto(appUser);
      List<SpotifyPlaylistItem> spotifyUserPublicPlaylists =
          spotifyPlaylistService.getUserPlaylists(spotifyUser);

      String appPlaylistId = appPlaylist.getId();

      Optional<SpotifyPlaylistItem> spotifyRunningWorkoutPlaylist =
          spotifyUserPublicPlaylists.stream()
              .filter(playlist -> playlist.getId().equals(appPlaylistId))
              .findFirst();

      boolean spotifyPlaylistExists = spotifyRunningWorkoutPlaylist.isPresent();

      if (!spotifyPlaylistExists) {

        String appUserId = appUser.getId();
        appUser.removePlaylist(appPlaylist);
        playlistRepository.deleteByRunnerId(appUserId);

        SpotifyPlaylistItemDetails spotifyPlaylistDetails = playlistConfig.getDetails();
        SpotifyPlaylistItem spotifyPlaylist =
            spotifyPlaylistService.createPlaylist(spotifyUser, spotifyPlaylistDetails);

        List<SpotifyTrackItem> spotifyAddTracks =
            appTracks.stream().map(appTrackMapper::mapToDto).toList();

        String spotifyPlaylistId = spotifyPlaylist.getId();
        spotifyPlaylistService.addTracks(spotifyPlaylistId, spotifyAddTracks);

        spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
        appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
        return playlistRepository.save((Playlist) appPlaylist);
      }

      SpotifyPlaylistItem spotifyPlaylist = spotifyRunningWorkoutPlaylist.get();
      String spotifyPlaylistId = spotifyPlaylist.getId();
      spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      List<SpotifyTrackItem> spotifyPlaylistTracks = spotifyPlaylist.getTracks().stream().toList();

      List<SpotifyTrackItem> spotifyAddTracks =
          appTracks.stream()
              .filter(Objects::nonNull)
              .filter(
                  appTrack ->
                      spotifyPlaylistTracks.stream()
                          .noneMatch(
                              spotifyPlaylistTrack ->
                                  spotifyPlaylistTrack.getId().equals(appTrack.getId())))
              .map(appTrackMapper::mapToDto)
              .toList();

      List<SpotifyTrackItem> spotifyRemoveTracks =
          spotifyPlaylistTracks.stream()
              .filter(Objects::nonNull)
              .filter(
                  spotifyPlaylistTrack ->
                      appTracks.stream()
                          .noneMatch(
                              appTrack -> appTrack.getId().equals(spotifyPlaylistTrack.getId())))
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
      return playlistRepository.save((Playlist) appPlaylist);
    } catch (RuntimeException e) {
      throw new AppPlaylistAddTracksException(appPlaylist.getId(), e);
    }
  }
}

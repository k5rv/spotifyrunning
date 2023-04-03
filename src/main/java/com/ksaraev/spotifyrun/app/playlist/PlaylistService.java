package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.CreateAppPlaylistException.*;

import com.ksaraev.spotifyrun.app.track.AppTrack;
import com.ksaraev.spotifyrun.app.track.AppTrackMapper;
import com.ksaraev.spotifyrun.app.user.*;
import com.ksaraev.spotifyrun.exception.business.*;
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
    String appPlaylistId = appPlaylist.getId();
    SpotifyPlaylistItem playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
    List<SpotifyTrackItem> playlistItemTracks = playlistItem.getTracks();

    List<AppTrack> playlistTracks =
        playlistItemTracks.stream()
            .filter(Objects::nonNull)
            .map(appTrackMapper::mapToEntity)
            .toList();

    List<AppTrack> tracksUpdate =
        appTracks.stream()
            .filter(
                appTrack ->
                    playlistTracks.stream()
                        .noneMatch(playlistTrack -> playlistTrack.getId().equals(appTrack.getId())))
            .toList();

    boolean isUpdateEmpty = tracksUpdate.isEmpty();
    boolean isPlaylistIdentical = appPlaylist.getSnapshotId().equals(playlistItem.getSnapshotId());

    if (isUpdateEmpty && !isPlaylistIdentical) {
      AppUser appUser = appPlaylist.getOwner();
      appUser.removePlaylist(appPlaylist);
      playlistRepository.deleteById(appPlaylistId);
      appPlaylist = playlistMapper.mapToEntity(playlistItem);
      return playlistRepository.save((Playlist) appPlaylist);
    }

    List<AppTrack> tracksRemove =
        playlistTracks.stream()
            .filter(
                playlistTrack ->
                    appTracks.stream()
                        .noneMatch(appTrack -> appTrack.getId().equals(playlistTrack.getId())))
            .toList();

    boolean isRemoveEmpty = tracksRemove.isEmpty();

    if (isRemoveEmpty) {
      List<SpotifyTrackItem> trackItemsUpdate =
          tracksUpdate.stream().map(appTrackMapper::mapToDto).toList();
      spotifyPlaylistService.addTracks(appPlaylistId, trackItemsUpdate);
      playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
      appPlaylist = playlistMapper.mapToEntity(playlistItem);
      return playlistRepository.save((Playlist) appPlaylist);
    }

    List<SpotifyTrackItem> trackItemsRemove =
        tracksRemove.stream().map(appTrackMapper::mapToDto).toList();
    spotifyPlaylistService.removeTracks(appPlaylistId, trackItemsRemove);
    List<SpotifyTrackItem> trackItemsUpdate =
        tracksUpdate.stream().map(appTrackMapper::mapToDto).toList();
    spotifyPlaylistService.addTracks(appPlaylistId, trackItemsUpdate);
    playlistItem = spotifyPlaylistService.getPlaylist(appPlaylistId);
    appPlaylist = playlistMapper.mapToEntity(playlistItem);
    return playlistRepository.save((Playlist) appPlaylist);
  }
}

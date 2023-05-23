package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunPlaylistRevisionService implements AppPlaylistRevisionService {

  private final AppTrackMapper trackMapper;

  public List<AppTrack> getAddedSourceTracks(
      @NotNull AppPlaylist appPlaylist, @NotNull SpotifyPlaylistItem spotifyPlaylist) {
    String playlistId = appPlaylist.getId();
    AppUser appUser = appPlaylist.getOwner();
    String userId = appUser.getId();
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
              .map(trackMapper::mapToEntity)
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
      throw new ReviseSuddenrunCustomUserTracksException(userId, playlistId, e);
    }
  }

  public List<AppTrack> getRemovedSourceTracks(
      @NotNull AppPlaylist target, @NotNull SpotifyPlaylistItem source) {
    String playlistId = target.getId();
    AppUser appUser = target.getOwner();
    String userId = appUser.getId();
    try {
      List<AppTrack> targetTracks = target.getTracks();
      List<AppTrack> rejectedTracks = target.getRejectedTracks();
      List<SpotifyTrackItem> sourceTracks = source.getTracks();

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
      throw new ReviseSuddenrunRejectedUserTracksException(userId, playlistId, e);
    }
  }

  @Override
  public List<SpotifyTrackItem> getSourceTracksToAdd(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> rejectedTracks) {
    return appTracks.stream()
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
                    .noneMatch(rejectedTrack -> rejectedTrack.getId().equals(appTrack.getId())))
        .map(trackMapper::mapToDto)
        .toList();
  }

  @Override
  public List<SpotifyTrackItem> getSourceTracksToRemove(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> customTracks) {
    return spotifyPlaylistTracks.stream()
        .filter(Objects::nonNull)
        .filter(
            spotifyPlaylistTrack ->
                appTracks.stream()
                    .noneMatch(appTrack -> appTrack.getId().equals(spotifyPlaylistTrack.getId())))
        .filter(
            appTrack ->
                customTracks.stream()
                    .noneMatch(customTrack -> customTrack.getId().equals(appTrack.getId())))
        .toList();
  }
}

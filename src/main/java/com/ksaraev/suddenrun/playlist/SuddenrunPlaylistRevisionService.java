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

  public List<AppTrack> getAddedTracks(
          @NotNull AppPlaylist targetPlaylist, @NotNull SpotifyPlaylistItem sourcePlaylist) {
    String playlistId = targetPlaylist.getId();
    AppUser appUser = targetPlaylist.getOwner();
    String userId = appUser.getId();
    try {
      List<AppTrack> targetTracks = targetPlaylist.getTracks();
      List<AppTrack> customTracks = targetPlaylist.getCustomTracks();
      List<SpotifyTrackItem> sourceTracks = sourcePlaylist.getTracks();

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

  public List<AppTrack> getRemovedTracks(
          @NotNull AppPlaylist targetPlaylist, @NotNull SpotifyPlaylistItem sourcePlaylist) {
    String playlistId = targetPlaylist.getId();
    AppUser appUser = targetPlaylist.getOwner();
    String userId = appUser.getId();
    try {
      List<AppTrack> targetTracks = targetPlaylist.getTracks();
      List<AppTrack> rejectedTracks = targetPlaylist.getRejectedTracks();
      List<SpotifyTrackItem> sourceTracks = sourcePlaylist.getTracks();

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
  public List<SpotifyTrackItem> getTracksToAdd(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> addedTracks) {
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
                addedTracks.stream()
                    .noneMatch(rejectedTrack -> rejectedTrack.getId().equals(appTrack.getId())))
        .map(trackMapper::mapToDto)
        .toList();
  }

  @Override
  public List<SpotifyTrackItem> getTracksToRemove(
      @NotNull List<AppTrack> appTracks,
      @NotNull List<SpotifyTrackItem> spotifyPlaylistTracks,
      @NotNull List<AppTrack> removedTracks) {
    return spotifyPlaylistTracks.stream()
        .filter(Objects::nonNull)
        .filter(
            spotifyPlaylistTrack ->
                appTracks.stream()
                    .noneMatch(appTrack -> appTrack.getId().equals(spotifyPlaylistTrack.getId())))
        .filter(
            appTrack ->
                removedTracks.stream()
                    .noneMatch(customTrack -> customTrack.getId().equals(appTrack.getId())))
        .toList();
  }
}

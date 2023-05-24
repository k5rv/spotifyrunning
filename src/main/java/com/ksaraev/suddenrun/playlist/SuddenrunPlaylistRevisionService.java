package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUser;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunPlaylistRevisionService implements AppPlaylistRevisionService {

  private final AppTrackMapper trackMapper;

  @Override
  public AppPlaylist updatePlaylist(@NotNull AppPlaylist actualPlaylist, @NotNull AppPlaylist sourcePlaylist) {
    List<AppTrack> sourceTracks = sourcePlaylist.getTracks();
    List<AppTrack> actualTrackPreferences = actualPlaylist.getPreferences();
    List<AppTrack> actualTrackExclusions = actualPlaylist.getExclusions();
    List<AppTrack> trackPreferences = matchSource(actualTrackPreferences, sourceTracks);
    List<AppTrack> trackExclusions = noneMatchSource(actualTrackExclusions, sourceTracks);
    sourcePlaylist.setPreferences(trackPreferences);
    sourcePlaylist.setExclusions(trackExclusions);
    return sourcePlaylist;
  }

  @Override
  public List<AppTrack> matchSource(
      @NotNull List<AppTrack> actualTracks, @NotNull List<AppTrack> sourceTracks) {
    if (actualTracks.isEmpty()) return List.of();
    if (sourceTracks.isEmpty()) return List.of();
    return actualTracks.stream()
        .filter(
            actual ->
                sourceTracks.stream().anyMatch(source -> source.getId().equals(actual.getId())))
        .toList();
  }

  @Override
  public List<AppTrack> noneMatchSource(
      @NotNull List<AppTrack> actualTracks, @NotNull List<AppTrack> sourceTracks) {
    if (actualTracks.isEmpty()) return List.of();
    if (sourceTracks.isEmpty()) return actualTracks;
    return actualTracks.stream()
        .filter(
            actual ->
                sourceTracks.stream().noneMatch(source -> source.getId().equals(actual.getId())))
        .toList();
  }

  public List<AppTrack> getAddedTracks(
      @NotNull AppPlaylist targetPlaylist, @NotNull SpotifyPlaylistItem sourcePlaylist) {
    String playlistId = targetPlaylist.getId();
    AppUser appUser = targetPlaylist.getUser();
    String userId = appUser.getId();
    try {
      List<AppTrack> targetTracks = targetPlaylist.getTracks();
      List<AppTrack> customTracks = targetPlaylist.getPreferences();
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
      throw new GetAddedTracksException(userId, playlistId, e);
    }
  }

  public List<AppTrack> getRemovedTracks(
      @NotNull AppPlaylist targetPlaylist, @NotNull SpotifyPlaylistItem sourcePlaylist) {
    String playlistId = targetPlaylist.getId();
    AppUser appUser = targetPlaylist.getUser();
    String userId = appUser.getId();
    try {
      List<AppTrack> targetTracks = targetPlaylist.getTracks();
      List<AppTrack> rejectedTracks = targetPlaylist.getExclusions();
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
      throw new GetRemovedTracksException(userId, playlistId, e);
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

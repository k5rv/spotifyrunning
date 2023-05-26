package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunPlaylistSynchronizationService implements AppPlaylistSynchronizationService {

  private final AppTrackMapper trackMapper;

  @Override
  public AppPlaylist updateFromSource(
      @NotNull AppPlaylist targetPlaylist, @NotNull AppPlaylist sourcePlaylist) {
    List<AppTrack> target = targetPlaylist.getTracks();
    List<AppTrack> source = sourcePlaylist.getTracks();
    List<AppTrack> targetInclusions = targetPlaylist.getInclusions();
    List<AppTrack> targetExclusions = targetPlaylist.getExclusions();
    List<AppTrack> inclusions = updateInclusions(source, target, targetInclusions);
    if (!inclusions.isEmpty()) log.info("Found [" + inclusions.size() + "] track inclusions");
    List<AppTrack> exclusions = updateExclusions(source, target, targetExclusions);
    if (!exclusions.isEmpty()) log.info("Found [" + exclusions.size() + "] track exclusions");
    sourcePlaylist.setInclusions(inclusions);
    sourcePlaylist.setExclusions(exclusions);
    return sourcePlaylist;
  }

  private List<AppTrack> updateInclusions(
          List<AppTrack> sourceTracks, List<AppTrack> targetTracks, List<AppTrack> targetInclusions) {
    List<AppTrack> inclusions = new ArrayList<>(targetInclusions);
    List<AppTrack> sourceDifference = findTracksNoneMatch(sourceTracks, targetTracks);
    List<AppTrack> addedPreferences = findTracksNoneMatch(sourceDifference, inclusions);
    inclusions.addAll(addedPreferences);
    List<AppTrack> removedPreferences = findTracksNoneMatch(inclusions, sourceTracks);
    inclusions.removeAll(removedPreferences);
    return inclusions;
  }

  private List<AppTrack> updateExclusions(
          List<AppTrack> sourceTracks, List<AppTrack> targetTracks, List<AppTrack> targetExclusions) {
    List<AppTrack> exclusions = new ArrayList<>(targetExclusions);
    List<AppTrack> targetDifference = findTracksNoneMatch(targetTracks, sourceTracks);
    List<AppTrack> addedExclusions = findTracksNoneMatch(targetDifference, exclusions);
    exclusions.addAll(addedExclusions);
    List<AppTrack> removedExclusions = findTracksMatch(exclusions, sourceTracks);
    exclusions.removeAll(removedExclusions);
    return exclusions;
  }

  @Override
  public List<AppTrack> findTracksNoneMatchPlaylist(
      @NotNull AppPlaylist appPlaylist, @NotNull List<AppTrack> appTracks) {
    if (appTracks.isEmpty()) return List.of();
    List<AppTrack> playlistTracks = appPlaylist.getTracks();
    List<AppTrack> tracksDifference = findTracksNoneMatch(appTracks, playlistTracks);
    List<AppTrack> playlistExclusions = appPlaylist.getExclusions();
    if (playlistExclusions.isEmpty()) return tracksDifference;
    return findTracksNoneMatch(tracksDifference, playlistExclusions);
  }

  @Override
  public List<AppTrack> findPlaylistNoneMatchTracks(
      @NotNull AppPlaylist appPlaylist, @NotNull List<AppTrack> appTracks) {
    List<AppTrack> playlistTracks = appPlaylist.getTracks();
    if (playlistTracks.isEmpty()) return List.of();
    List<AppTrack> playlistDifference = findTracksNoneMatch(playlistTracks, appTracks);
    List<AppTrack> playlistInclusions = appPlaylist.getInclusions();
    if (playlistInclusions.isEmpty()) return playlistDifference;
    return findTracksNoneMatch(playlistDifference, playlistInclusions);
  }


  private List<AppTrack> findTracksMatch(
      @NotNull List<AppTrack> comparisonSourceTracks,
      @NotNull List<AppTrack> comparisonTargetTracks) {
    if (comparisonSourceTracks.isEmpty()) return List.of();
    if (comparisonTargetTracks.isEmpty()) return List.of();
    return comparisonSourceTracks.stream()
        .filter(
            actual ->
                comparisonTargetTracks.stream()
                    .anyMatch(source -> source.getId().equals(actual.getId())))
        .toList();
  }

  private List<AppTrack> findTracksNoneMatch(
      @NotNull List<AppTrack> comparisonSourceTracks,
      @NotNull List<AppTrack> comparisonTargetTracks) {
    if (comparisonSourceTracks.isEmpty()) return List.of();
    if (comparisonTargetTracks.isEmpty()) return comparisonSourceTracks;
    return comparisonSourceTracks.stream()
        .filter(
            source ->
                comparisonTargetTracks.stream()
                    .noneMatch(target -> target.getId().equals(source.getId())))
        .toList();
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

package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItemDetails;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItem;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichItemRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichSpotifyItemRequest;
import com.ksaraev.spotifyrunning.model.playlist.AbstractPlaylistMapper;
import com.ksaraev.spotifyrunning.model.playlist.Playlist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PlaylistService {

  private final SpotifyClient spotifyClient;
  private final AbstractPlaylistMapper abstractPlaylistMapper;

  @Valid
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails) {

    SpotifyItemDetails spotifyItemDetails =
        abstractPlaylistMapper.toPlaylistItemDetails(playlistDetails);

    SpotifyItem spotifyItem = spotifyClient.createPlaylist(user.getId(), spotifyItemDetails);

    if (spotifyItem == null) {
      throw new NullPointerException("Spotify playlist is null");
    }

    PlaylistItem playlistItem = (PlaylistItem) spotifyItem;

    Playlist playlist = abstractPlaylistMapper.toPlaylist(playlistItem);
    log.info("Playlist created: {}", playlist);
    return playlist;
  }

  public SpotifyPlaylist addTracks(
      @NotNull SpotifyPlaylist playlist, @NotEmpty List<SpotifyTrack> tracks) {

    List<SpotifyEntity> spotifyEntities =
        tracks.stream().map(SpotifyEntity.class::cast).collect(Collectors.toList());

    List<List<SpotifyEntity>> entityBatchList = Lists.partition(spotifyEntities, 99);

    IntStream.range(0, entityBatchList.size())
        .forEach(
            index -> {
              log.info(
                  "Updating playlist (id: {}, snapshotId: {})",
                  playlist.getId(),
                  playlist.getSnapshotId());

              String snapshotId = addPlaylistEntities(playlist, entityBatchList.get(index));

              if (Objects.isNull(snapshotId)) {
                throw new RuntimeException("Snapshot id is null");
              }
            });

    SpotifyPlaylist updatedPlaylist = getPlaylist(playlist);

    if (Objects.isNull(updatedPlaylist)) {
      throw new RuntimeException("Updated playlist is null");
    }

    if (Objects.isNull(updatedPlaylist.getTracks())) {
      throw new RuntimeException("Updated playlist track list is null");
    }

    if (updatedPlaylist.getTracks().isEmpty()) {
      throw new RuntimeException("Updated playlist is empty");
    }

    List<SpotifyTrack> diffTracks =
        updatedPlaylist.getTracks().stream()
            .filter(
                updatedPlaylistTrack ->
                    tracks.stream()
                        .anyMatch(
                            trackToAdd -> trackToAdd.getId().equals(updatedPlaylistTrack.getId())))
            .collect(Collectors.toList());

    if (diffTracks.isEmpty()) {
      log.warn("Playlist tracks haven't changed");
      return playlist;
    }

    log.info("Playlist (id:{}), updated with tracks {}", playlist.getId(), diffTracks);
    return playlist;
  }

  private String addPlaylistEntities(
      @NotNull SpotifyPlaylist playlist, @NotEmpty List<SpotifyEntity> entities) {

    List<URI> entityUris =
        entities.stream().map(SpotifyEntity::getUri).collect(Collectors.toList());

    EnrichSpotifyItemRequest request = EnrichItemRequest.builder().uris(entityUris).build();

    log.info(
        "Prepared {} entities for updating playlist {}: {}", entities.size(), playlist, entities);

    String snapshotId = spotifyClient.addPlaylistItems(playlist.getId(), request);

    if (Objects.isNull(snapshotId)) {
      throw new IllegalArgumentException("Spotify playlist add tracks response is null");
    }

    if (playlist.getSnapshotId().equals(snapshotId)) {
      log.warn(
          "Spotify playlist tracks updated successfully but snapshotId:{} hasn't changed, check back later",
          snapshotId);
      return snapshotId;
    }

    playlist.setSnapshotId(snapshotId);

    log.info(
        "Spotify playlist (id: {}, name: {}, user: {}, snapshotId: {}) entities updated",
        playlist.getId(),
        playlist.getName(),
        playlist.getOwner().getId(),
        playlist.getSnapshotId());
    return snapshotId;
  }

  public SpotifyPlaylist getPlaylist(@Valid @NotNull SpotifyPlaylist playlist) {

    if (Objects.isNull(playlist)) {
      throw new IllegalArgumentException("Playlist is null");
    }
    return getPlaylist(playlist.getId());
  }

  private SpotifyPlaylist getPlaylist(@NotNull String playlistId) {

    SpotifyItem spotifyItem = spotifyClient.getPlaylist(playlistId);

    if (spotifyItem == null) {
      throw new NullPointerException("Spotify playlist is null");
    }

    PlaylistItem playlistItem = (PlaylistItem) spotifyItem;

    SpotifyPlaylist playlist = abstractPlaylistMapper.toPlaylist(playlistItem);

    log.info("Playlist received: {}", playlist);
    return playlist;
  }
}

package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItemDetails;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItem;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichItemRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichSpotifyItemRequest;
import com.ksaraev.spotifyrunning.model.playlist.Playlist;
import com.ksaraev.spotifyrunning.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PlaylistService implements SpotifyPlaylistService {

  private final SpotifyClient spotifyClient;
  private final PlaylistMapper playlistMapper;

  @Override
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails) {
    log.info("Creating playlist for user: {}", user);
    SpotifyItemDetails itemDetails = playlistMapper.toPlaylistItemDetails(playlistDetails);

    SpotifyItem item = spotifyClient.createPlaylist(user.getId(), itemDetails);

    if (Objects.isNull(item)) {
      throw new IllegalStateException("Playlist response is null");
    }

    PlaylistItem playlistItem = (PlaylistItem) item;

    Playlist playlist = playlistMapper.toPlaylist(playlistItem);
    log.info("Playlist created: {}", playlist);
    return playlist;
  }

  @Override
  public void addTracks(@NotNull SpotifyPlaylist playlist, @NotNull List<SpotifyTrack> tracks) {
    log.info("Adding tracks to playlist: {}", playlist);

    List<List<SpotifyTrack>> trackBatches = Lists.partition(tracks, 99);

    String playlistId = playlist.getId();

    IntStream.range(0, trackBatches.size())
        .forEach(
            index -> {
              List<SpotifyTrack> trackBatch = trackBatches.get(index);

              List<URI> trackUris = trackBatch.stream().map(SpotifyTrack::getUri).toList();

              EnrichSpotifyItemRequest request =
                  EnrichItemRequest.builder().uris(trackUris).build();

              String snapshotId = spotifyClient.addPlaylistItems(playlistId, request);

              if (Objects.isNull(snapshotId)) {
                throw new IllegalStateException("Adding tracks response is null");
              }

              playlist.setSnapshotId(snapshotId);
              log.info("Added {} tracks to playlist: {}", trackBatch.size(), playlist);
            });
  }

  @Override
  public SpotifyPlaylist getPlaylist(@NotNull String playlistId) {
    log.info("Getting playlist with id: {}", playlistId);
    SpotifyItem item = spotifyClient.getPlaylist(playlistId);

    if (Objects.isNull(item)) {
      throw new NullPointerException("Playlist response is null");
    }

    PlaylistItem playlistItem = (PlaylistItem) item;

    Playlist playlist = playlistMapper.toPlaylist(playlistItem);

    log.info("Playlist received: {}", playlist);
    return playlist;
  }
}

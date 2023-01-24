package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItemDetails;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItem;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichItemRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichSpotifyItemRequest;
import com.ksaraev.spotifyrunning.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PlaylistService implements SpotifyPlaylistService {

  private final SpotifyClient spotifyClient;
  private final PlaylistMapper playlistMapper;

  @Override
  public SpotifyPlaylist getPlaylist(@NotNull String playlistId) {
    SpotifyItem item = spotifyClient.getPlaylist(playlistId);
    PlaylistItem playlistItem = (PlaylistItem) item;
    return playlistMapper.toPlaylist(playlistItem);
  }

  @Override
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails) {
    SpotifyItemDetails itemDetails = playlistMapper.toPlaylistItemDetails(playlistDetails);
    SpotifyItem item = spotifyClient.createPlaylist(user.getId(), itemDetails);
    PlaylistItem playlistItem = (PlaylistItem) item;
    return playlistMapper.toPlaylist(playlistItem);
  }

  @Override
  public void addTracks(@NotNull SpotifyPlaylist playlist, @NotNull List<SpotifyTrack> tracks) {
    List<List<SpotifyTrack>> trackBatches = Lists.partition(tracks, 99);
    IntStream.range(0, trackBatches.size())
        .forEach(
            index -> {
              List<SpotifyTrack> trackBatch = trackBatches.get(index);
              List<URI> trackUris = trackBatch.stream().map(SpotifyTrack::getUri).toList();
              EnrichSpotifyItemRequest request =
                  EnrichItemRequest.builder().uris(trackUris).build();
              spotifyClient.addPlaylistItems(playlist.getId(), request);
            });
  }
}

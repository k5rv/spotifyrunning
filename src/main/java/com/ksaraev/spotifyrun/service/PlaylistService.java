package com.ksaraev.spotifyrun.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
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
  public SpotifyPlaylist createPlaylist(SpotifyUser user, SpotifyPlaylistDetails playlistDetails) {
    SpotifyPlaylistItemDetails playlistItemDetails = playlistMapper.toSpotifyItem(playlistDetails);
    SpotifyPlaylistItem playlistItem =
        spotifyClient.createPlaylist(user.getId(), playlistItemDetails);
    return playlistMapper.toModel(playlistItem);
  }

  @Override
  public SpotifyPlaylist getPlaylist(String playlistId) {
    SpotifyPlaylistItem playlistItem = spotifyClient.getPlaylist(playlistId);
    return playlistMapper.toModel(playlistItem);
  }

  @Override
  public void addTracks(SpotifyPlaylist playlist, List<SpotifyTrack> tracks) {
    List<List<SpotifyTrack>> trackBatches = Lists.partition(tracks, 99);
    IntStream.range(0, trackBatches.size())
        .forEach(
            index -> {
              List<SpotifyTrack> trackBatch = trackBatches.get(index);
              List<URI> trackUris = trackBatch.stream().map(SpotifyTrack::getUri).toList();
              AddItemsRequest request = new AddItemsRequest(trackUris);
              spotifyClient.addItemsToPlaylist(playlist.getId(), request);
            });
  }
}

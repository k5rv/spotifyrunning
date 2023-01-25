package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.SpotifyPlaylistDTO;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.details.SpotifyPlaylistDetailsDTO;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichItemRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.EnrichSpotifyItemRequest;
import com.ksaraev.spotifyrunning.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrunning.model.playlist.details.PlaylistDetailsMapper;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyUser;
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
  private final PlaylistDetailsMapper playlistDetailsMapper;

  @Override
  public SpotifyPlaylist getPlaylist(@NotNull String playlistId) {
    SpotifyPlaylistDTO playlistDTO = (SpotifyPlaylistDTO) spotifyClient.getPlaylist(playlistId);
    return playlistMapper.toModel(playlistDTO);
  }

  @Override
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyUser user, @NotNull SpotifyPlaylistDetails playlistDetails) {
    SpotifyPlaylistDetailsDTO playlistDetailsDTO = playlistDetailsMapper.toDto(playlistDetails);
    SpotifyPlaylistDTO playlistDTO =
        (SpotifyPlaylistDTO) spotifyClient.createPlaylist(user.getId(), playlistDetailsDTO);
    return playlistMapper.toModel(playlistDTO);
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

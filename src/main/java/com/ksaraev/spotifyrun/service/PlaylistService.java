package com.ksaraev.spotifyrun.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyForbiddenException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyTooManyRequestsException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.client.requests.AddItemsRequest;
import com.ksaraev.spotifyrun.exception.service.AddTracksException;
import com.ksaraev.spotifyrun.exception.service.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.service.GetPlaylistException;
import com.ksaraev.spotifyrun.exception.spotify.ForbiddenException;
import com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException;
import com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException;
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

import static com.ksaraev.spotifyrun.exception.service.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.service.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.service.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.spotify.ForbiddenException.FORBIDDEN;
import static com.ksaraev.spotifyrun.exception.spotify.TooManyRequestsException.TOO_MANY_REQUESTS;
import static com.ksaraev.spotifyrun.exception.spotify.UnauthorizedException.UNAUTHORIZED;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PlaylistService implements SpotifyPlaylistService {

  private final SpotifyClient spotifyClient;
  private final PlaylistMapper playlistMapper;

  @Override
  public SpotifyPlaylist createPlaylist(SpotifyUser user, SpotifyPlaylistDetails playlistDetails) {
    try {
      SpotifyPlaylistItemDetails playlistItemDetails =
          playlistMapper.mapToPlaylistItemDetails(playlistDetails);
      SpotifyPlaylistItem playlistItem =
          spotifyClient.createPlaylist(user.getId(), playlistItemDetails);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + e.getMessage(), e);
    } catch (SpotifyForbiddenException e) {
      throw new ForbiddenException(FORBIDDEN + e.getMessage(), e);
    } catch (SpotifyTooManyRequestsException e) {
      throw new TooManyRequestsException(TOO_MANY_REQUESTS + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new CreatePlaylistException(UNABLE_TO_CREATE_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public SpotifyPlaylist getPlaylist(String playlistId) throws GetPlaylistException {
    try {
      SpotifyPlaylistItem playlistItem = spotifyClient.getPlaylist(playlistId);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + e.getMessage(), e);
    } catch (SpotifyForbiddenException e) {
      throw new ForbiddenException(FORBIDDEN + e.getMessage(), e);
    } catch (SpotifyTooManyRequestsException e) {
      throw new TooManyRequestsException(TOO_MANY_REQUESTS + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetPlaylistException(UNABLE_TO_GET_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public void addTracks(SpotifyPlaylist playlist, List<SpotifyTrack> tracks) {
    try {
      List<List<SpotifyTrack>> trackBatches = Lists.partition(tracks, 99);
      IntStream.range(0, trackBatches.size())
          .forEach(
              index -> {
                List<SpotifyTrack> trackBatch = trackBatches.get(index);
                List<URI> trackUris = trackBatch.stream().map(SpotifyTrack::getUri).toList();
                AddItemsRequest request = new AddItemsRequest(trackUris);
                spotifyClient.addItemsToPlaylist(playlist.getId(), request);
              });
    } catch (SpotifyUnauthorizedException e) {
      throw new UnauthorizedException(UNAUTHORIZED + e.getMessage(), e);
    } catch (SpotifyForbiddenException e) {
      throw new ForbiddenException(FORBIDDEN + e.getMessage(), e);
    } catch (SpotifyTooManyRequestsException e) {
      throw new TooManyRequestsException(TOO_MANY_REQUESTS + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new AddTracksException(UNABLE_TO_ADD_TRACKS + e.getMessage(), e);
    }
  }
}

package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.AddTracksException.UNABLE_TO_ADD_TRACKS;
import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.UNABLE_TO_CREATE_PLAYLIST;
import static com.ksaraev.spotifyrun.exception.business.GetPlaylistException.UNABLE_TO_GET_PLAYLIST;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.AddItemsRequest;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.exception.business.AddTracksException;
import com.ksaraev.spotifyrun.exception.business.CreatePlaylistException;
import com.ksaraev.spotifyrun.exception.business.GetPlaylistException;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;
import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PlaylistService implements SpotifyPlaylistService {

  private final SpotifyClient spotifyClient;
  private final PlaylistMapper playlistMapper;

  @Override
  public SpotifyPlaylist getPlaylist(String playlistId) throws GetPlaylistException {
    try {
      SpotifyPlaylistItem playlistItem = spotifyClient.getPlaylist(playlistId);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (RuntimeException e) {
      throw new GetPlaylistException(UNABLE_TO_GET_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public SpotifyPlaylist createPlaylist(SpotifyUser user, SpotifyPlaylistDetails playlistDetails) {
    try {
      SpotifyPlaylistItemDetails playlistItemDetails =
          playlistMapper.mapToPlaylistItemDetails(playlistDetails);
      SpotifyPlaylistItem playlistItem =
          spotifyClient.createPlaylist(user.getId(), playlistItemDetails);
      return playlistMapper.mapToPlaylist(playlistItem);
    } catch (RuntimeException e) {
      throw new CreatePlaylistException(UNABLE_TO_CREATE_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public void addTracks(SpotifyPlaylist playlist, List<SpotifyTrack> tracks) {
    try {
      List<URI> trackUris = tracks.stream().map(SpotifyTrack::getUri).toList();
      AddItemsRequest request = new AddItemsRequest(trackUris);
      spotifyClient.addItemsToPlaylist(playlist.getId(), request);
    } catch (RuntimeException e) {
      throw new AddTracksException(UNABLE_TO_ADD_TRACKS + e.getMessage(), e);
    }
  }
}

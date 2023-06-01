package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class SuddenrunPlaylistController {

  private final AppTrackService trackService;

  private final AppPlaylistService playlistService;

  @PutMapping("/{playlist_id}/tracks")
  public AddPlaylistTracksResponse addTracks(
      @PathVariable(value = "playlist_id") String playlistId) {
    try {
      AppPlaylist appPlaylist =
          playlistService
              .getPlaylist(playlistId)
              .orElseThrow(() -> new SuddenrunPlaylistDoesNotExistException(playlistId));
      List<AppTrack> appTracks = trackService.getTracks();
      appPlaylist = playlistService.addTracks(appPlaylist, appTracks);
      List<String> trackIds = appPlaylist.getTracks().stream().map(AppTrack::getId).toList();
      return AddPlaylistTracksResponse.builder().id(playlistId).trackIds(trackIds).build();
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }
}

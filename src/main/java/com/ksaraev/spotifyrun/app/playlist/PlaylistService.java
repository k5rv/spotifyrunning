package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.AddMusicRecommendationsException.*;
import static com.ksaraev.spotifyrun.exception.business.CreateAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.GetAppPlaylistException.*;
import static com.ksaraev.spotifyrun.exception.business.PlaylistExistenceException.*;
import static com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException.*;

import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.Runner;
import com.ksaraev.spotifyrun.exception.business.*;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.service.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService implements AppPlaylistService {

  private final PlaylistMapper mapper;
  private final PlaylistRepository repository;
  private final AppPlaylistConfig config;
  private final SpotifyPlaylistItemService playlistItemService;
  private final SpotifyUserTopTrackItemsService topTrackItemsService;
  private final SpotifyUserProfileItemService userProfileItemService;
  private final SpotifyRecommendationItemsService recommendationsService;

  @Override
  public boolean playlistExists(AppUser appUser) {
    try {
      UUID uuid = appUser.getUuid();
      return repository.existsByRunnerUuid(uuid);
    } catch (RuntimeException e) {
      throw new PlaylistExistenceException(UNABLE_TO_GET_PLAYLIST_STATUS + e.getMessage(), e);
    }
  }

  @Override
  public AppPlaylist getPlaylist(AppUser appUser) {
    try {
      UUID uuid = appUser.getUuid();
      return repository.findByRunnerUuid(uuid);
    } catch (RuntimeException e) {
      throw new GetAppPlaylistException(UNABLE_TO_GET_APP_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public AppPlaylist createPlaylist(AppUser appUser) {
    try {
      SpotifyUserProfileItem userProfileItem = userProfileItemService.getCurrentUser();
      SpotifyPlaylistItemDetails playlistItemDetails = config.getDetails();
      SpotifyPlaylistItem playlistItem =
          playlistItemService.createPlaylist(userProfileItem, playlistItemDetails);
      Playlist playlist = mapper.updateEntity(playlistItem);
      playlist.setRunner((Runner) appUser);
      return repository.save(playlist);
    } catch (RuntimeException e) {
      throw new CreateAppPlaylistException(UNABLE_TO_CREATE_APP_PLAYLIST + e.getMessage(), e);
    }
  }

  @Override
  public void addMusicRecommendations(AppPlaylist appPlaylist) {
    try {
      List<SpotifyTrackItem> topTracks = getTopTracks();
      List<SpotifyTrackItem> recommendations = getRecommendations(topTracks);
      String id = appPlaylist.getExternalId();
      playlistItemService.addTracks(id, recommendations);
      SpotifyPlaylistItem playlistItem = playlistItemService.getPlaylist(id);
      Playlist playlist = mapper.updateEntity((Playlist) appPlaylist, playlistItem);
      repository.save(playlist);
    } catch (RuntimeException e) {
      throw new AddMusicRecommendationsException(
          UNABLE_TO_ADD_MUSIC_RECOMMENDATIONS + e.getMessage(), e);
    }
  }

  private List<SpotifyTrackItem> getTopTracks() {
    List<SpotifyTrackItem> topTracks = topTrackItemsService.getUserTopTracks();
    if (topTracks.isEmpty()) {
      throw new UserTopTracksNotFoundException(
          UserTopTracksNotFoundException.USER_TOP_TRACKS_NOT_FOUND);
    }
    return topTracks;
  }

  private List<SpotifyTrackItem> getRecommendations(List<SpotifyTrackItem> trackItems) {
    List<SpotifyTrackItem> musicRecommendations =
        trackItems.stream()
            .map(
                track ->
                    recommendationsService.getRecommendations(
                        List.of(track), config.getMusicFeatures()))
            .flatMap(List::stream)
            .distinct()
            .limit(config.getSize())
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                      Collections.shuffle(list);
                      return list;
                    }));

    if (musicRecommendations.isEmpty()) {
      throw new RecommendationsNotFoundException(RECOMMENDATIONS_NOT_FOUND);
    }
    return musicRecommendations;
  }
}

package com.ksaraev.spotifyrun.controller;

import static com.ksaraev.spotifyrun.exception.business.AuthenticationException.*;

import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.exception.business.AuthenticationException;
import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import com.ksaraev.spotifyrun.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/playlists")
@AllArgsConstructor
public class PlaylistController {
  private final SpotifyUserService spotifyUserService;
  private final SpotifyUserTopTracksService topTracksService;
  private final SpotifyRecommendationsService recommendationsService;
  private final SpotifyPlaylistService playlistService;


  private final AppUserService appUserService;

  @PostMapping
  public SpotifyPlaylist createPlaylist() {
    SpotifyUser spotifyUser = appUserService.getAuthenticatedUser();
//    if (spotifyUser == null) {
//      throw new AuthenticationException(UNABLE_TO_GET_USER_AUTHENTICATION_DETAILS);
//    }
//    String id = spotifyUser.getId();
//    boolean isUserRegistered = appUserService.isUserRegistered(id);
//
//    if (!isUserRegistered) {
//      spotifyUser = spotifyUserService.getUser(id);
//      appUserService.registerUser(spotifyUser);
//    }

    boolean hasPlaylist = appUserService.hasPlaylist(spotifyUser);
    if(!hasPlaylist) {
      SpotifyPlaylist  spotifyPlaylist = appUserService.createPlaylist(spotifyUser);
    }

    /*
    user exist?
    yes -> move to recommendations
    no ->  save

    playlist for this user exist?
    no ->
    1. retrieve top tracks from spotify
    2. retrieve recommendations based on top tracks and playlist size limit
    3. save recommendations with top tracks seed

    recommendations for this user exist?
    yes ->
    1. retrieve top tracks
    2. get recommendations based on top tracks that not already in db and (playlist size limit - existing recommendations)
    4. update existing recommendations
    if somehow recommendations exists for not existing user log warning, and start scenario for existing user with recommendations
    no ->
    1. retrieve top tracks from spotify
    2. retrieve recommendations based on top tracks and playlist size limit
    3. save recommendations with top tracks seed

    playlist exist?
    yes ->
    1. get stored recommendations
    2. get stored playlist
    3. get tracks from recommendations that not already in playlist
    4. add new tracks
    5. save playlist
    6. get tracks from old playlist that not in new
    7. remove redundant music tracks
    no ->
    1. create in spotify
    2. save
    3. get recommendations
    4. add recommended tracks to playlist in spotify
    5. update playlist
     */

    /*    SpotifyUser user = userService.getCurrentUser();
    List<SpotifyTrack> userTopTracks = topTracksService.getUserTopTracks();

    if (userTopTracks.isEmpty()) {
      throw new UserTopTracksNotFoundException(USER_TOP_TRACKS_NOT_FOUND);
    }

    List<SpotifyTrack> musicRecommendations =
        userTopTracks.stream()
            .map(
                userTopTrack ->
                    recommendationsService.getRecommendations(
                        List.of(userTopTrack), playlistConfig.getMusicFeatures()))
            .flatMap(List::stream)
            .distinct()
            .limit(playlistConfig.getSize())
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

    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistConfig.getDetails());
    playlistService.addTracks(playlist, musicRecommendations);
    return playlistService.getPlaylist(playlist.getId());*/
    return null;
  }
}

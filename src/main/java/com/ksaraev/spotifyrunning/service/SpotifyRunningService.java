package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetUserTopItemsRequest;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.playlist.Playlist;
import com.ksaraev.spotifyrunning.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.recommendation.RecommendationFeatures;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class SpotifyRunningService {

  private final UserService userService;
  private final ArtistService artistService;
  private final PlaylistService playlistService;
  private final RecommendationService recommendationService;

  public SpotifyPlaylist getPlaylist() {

    boolean isExist = false;

    if (!isExist) {
      SpotifyRecommendationFeatures features =
          RecommendationFeatures.builder()
              .minEnergy(BigDecimal.valueOf(0.65))
              .minTempo(BigDecimal.valueOf(185.00))
              .maxTempo(BigDecimal.valueOf(205.00))
              .build();

      String name =
          String.format(
              "Running workout %s:%s",
              LocalDateTime.now().getHour(), LocalDateTime.now().getMinute());

      SpotifyPlaylistDetails details = PlaylistDetails.builder().name(name).build();
      return createPlaylist(details, features);
    }

    return new Playlist();
  }

  private SpotifyPlaylist createPlaylist(
      SpotifyPlaylistDetails playlistDetails, SpotifyRecommendationFeatures features) {
    List<SpotifyTrack> tracks = userService.getTopTracks(GetUserTopItemsRequest.builder().build());

    if (CollectionUtils.isEmpty(tracks)) {
      throw new RuntimeException(
          String.format("Top tracks list value is: %s, expected not to be null or empty", tracks));
    }

    List<SpotifyArtist> artists = artistService.getArtists(tracks);

    if (CollectionUtils.isEmpty(artists)) {
      throw new RuntimeException(
          String.format("Artists list value is: %s, expected not to be null or empty", artists));
    }

    List<SpotifyTrack> tracksRecommendations =
        recommendationService.getTracksRecommendation(tracks, artists, features);

    if (CollectionUtils.isEmpty(tracksRecommendations)) {
      throw new RuntimeException(
          String.format(
              "Track recommendation list value is: %s, expected not to be null or empty", artists));
    }

    SpotifyUser user = userService.getUser();

    if (Objects.isNull(user)) {
      throw new RuntimeException("User is null");
    }

    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistDetails);

    if (Objects.isNull(playlist)) {
      throw new RuntimeException("Playlist is null");
    }

    return playlistService.addTracks(playlist, tracksRecommendations);
  }
}

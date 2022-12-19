package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetUserTopItemsRequest;
import com.ksaraev.spotifyrunning.config.SpotifyRunningConfiguration;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class SpotifyRunningService implements SpotifyRunning {

  private final SpotifyUserService userService;
  private final SpotifyArtistService artistService;
  private final SpotifyPlaylistService playlistService;
  private final SpotifyRecommendationService recommendationService;
  private final SpotifyRunningConfiguration spotifyRunningConfiguration;

  public SpotifyPlaylist getPlaylist() {

    return createPlaylist(
        spotifyRunningConfiguration.spotifyPlaylistDetails(),
        spotifyRunningConfiguration.spotifyRecommendationFeatures());
  }

  private SpotifyPlaylist createPlaylist(
      @NotNull SpotifyPlaylistDetails playlistDetails,
      @NotNull SpotifyRecommendationFeatures features) {

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

    if (artists.stream().allMatch(artist -> Objects.isNull(artist.getGenres()))) {
      throw new RuntimeException(
          String.format("Artists genres are null, check artists seed: %s", artists));
    }

    List<String> genresSeed =
        artists.stream().map(SpotifyArtist::getGenres).flatMap(List::stream).toList();

    List<List<String>> genresSeedList = Lists.partition(genresSeed, 1);

    List<List<SpotifyTrack>> tracksSeedList = Lists.partition(tracks, 1);

    List<List<SpotifyArtist>> artistsSeedList = Lists.partition(artists, 1);

    AtomicReference<Set<SpotifyTrack>> trackSetAtomicReference = new AtomicReference<>();
    trackSetAtomicReference.set(new HashSet<>());

    IntStream.range(
            0,1/*
            Math.min(
                Math.min(tracksSeedList.size(), artistsSeedList.size()), genresSeedList.size())*/)
        .forEach(
            index -> {
              List<SpotifyTrack> tracksRecommendation =
                  recommendationService.getTracksRecommendation(
                      tracksSeedList.get(index),
                      artistsSeedList.get(index),
                      genresSeedList.get(index),
                      features);

              trackSetAtomicReference.get().addAll(new HashSet<>(tracksRecommendation));
            });

    log.info(
        "{} Tracks recommendations are ready: {}",
        trackSetAtomicReference.get().size(),
        trackSetAtomicReference.get());
    List<SpotifyTrack> tracksRecommendations = trackSetAtomicReference.get().stream().toList();

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

package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetUserTopItemsRequest;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class RunningPlaylistService implements SpotifyRunningPlaylistService {

  private final SpotifyUserService userService;
  private final SpotifyArtistService artistService;
  private final SpotifyPlaylistService playlistService;
  private final SpotifyRecommendationService recommendationService;

  @Override
  public SpotifyPlaylist createPlaylist(
      @NotNull SpotifyPlaylistDetails playlistDetails, SpotifyRecommendationFeatures features) {
    List<SpotifyTrack> userTopTracks =
        userService.getTopTracks(GetUserTopItemsRequest.builder().build());

    if (userTopTracks.isEmpty()) {
      throw new IllegalStateException("Top tracks not found");
    }

    List<String> artistIds =
        userTopTracks.stream()
            .map(SpotifyTrack::getArtists)
            .flatMap(List::stream)
            .map(SpotifyArtist::getId)
            .distinct()
            .toList();

    List<SpotifyArtist> topTracksArtists = artistService.getArtists(artistIds);

    if (topTracksArtists.isEmpty()) {
      throw new IllegalStateException("Artists not found");
    }

    if (topTracksArtists.stream().allMatch(artist -> Objects.isNull(artist.getGenres()))) {
      throw new IllegalStateException("Artists genres not found");
    }

    List<String> topTracksGenres =
        topTracksArtists.stream().map(SpotifyArtist::getGenres).flatMap(List::stream).toList();

    List<List<SpotifyTrack>> seedTracksPartitions = Lists.partition(userTopTracks, 1);

    List<List<String>> seedGenresPartitions = Lists.partition(topTracksGenres, 1);

    List<List<SpotifyArtist>> seedArtistsPartitions = Lists.partition(topTracksArtists, 1);

    AtomicReference<Set<SpotifyTrack>> tracks = new AtomicReference<>();
    tracks.set(new HashSet<>());

    IntStream.range(0, 1 /*
            Math.min(
                Math.min(tracksSeedList.size(), artistsSeedList.size()), genresSeedList.size())*/)
        .forEach(
            index -> {
              List<SpotifyTrack> seedRecommendations =
                  recommendationService.getTracks(
                      seedTracksPartitions.get(index),
                      seedArtistsPartitions.get(index),
                      seedGenresPartitions.get(index),
                      features);

              tracks.get().addAll(new HashSet<>(seedRecommendations));
            });

    if (tracks.get().stream().toList().isEmpty()) {
      throw new IllegalStateException("Recommendations not found");
    }

    SpotifyUser user = userService.getUser();

    SpotifyPlaylist playlist = playlistService.createPlaylist(user, playlistDetails);

    playlistService.addTracks(playlist, tracks.get().stream().toList());

    return playlistService.getPlaylist(playlist.getId());
  }
}

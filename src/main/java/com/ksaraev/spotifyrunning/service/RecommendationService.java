package com.ksaraev.spotifyrunning.service;

import com.google.common.collect.Lists;
import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.client.dto.responses.UserRecommendedItemsResponse;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.recommendation.AbstractRecommendationMapper;
import com.ksaraev.spotifyrunning.model.track.AbstractTrackMapper;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RecommendationService {
  private final SpotifyClient spotifyClient;
  private final AbstractTrackMapper trackMapper;
  private final AbstractRecommendationMapper recommendationMapper;

  public List<SpotifyTrack> getTracksRecommendation(
      @NotEmpty List<SpotifyTrack> tracksSeed,
      @NotEmpty List<SpotifyArtist> artistsSeed,
      @NotNull SpotifyRecommendationFeatures recommendationFeatures) {

    if (artistsSeed.stream().allMatch(artist -> Objects.isNull(artist.getGenres()))) {
      throw new RuntimeException(
          String.format("Artists genres are null, check artists seed: %s", artistsSeed));
    }

    List<String> genresSeed =
        artistsSeed.stream()
            .map(SpotifyArtist::getGenres)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    List<List<String>> genresSeedList = Lists.partition(genresSeed, 1);

    List<List<SpotifyTrack>> tracksSeedList = Lists.partition(tracksSeed, 1);

    List<List<SpotifyArtist>> artistsSeedList = Lists.partition(artistsSeed, 1);

    AtomicReference<Set<SpotifyTrack>> trackSetAtomicReference = new AtomicReference<>();
    trackSetAtomicReference.set(new HashSet<>());

    IntStream.range(0, 1 /*Math.min(
                Math.min(tracksSeedList.size(), artistsSeedList.size()), genresSeedList.size())*/)
        .forEach(
            index -> {
              List<SpotifyTrack> tracksRecommendation =
                  getTracksRecommendation(
                      tracksSeedList.get(index),
                      artistsSeedList.get(index),
                      genresSeedList.get(index),
                      recommendationFeatures);

              trackSetAtomicReference.get().addAll(new HashSet<>(tracksRecommendation));
            });

    log.info(
        "{} Tracks recommendations are ready: {}",
        trackSetAtomicReference.get().size(),
        trackSetAtomicReference.get());
    return trackSetAtomicReference.get().stream().toList();
  }

  public List<SpotifyTrack> getTracksRecommendation(
      @Size(min = 1, max = 5) List<SpotifyTrack> tracksSeed,
      @Size(min = 1, max = 5) List<SpotifyArtist> artistsSeed,
      @Size(min = 1, max = 5) List<String> genresSeed,
      @NotNull SpotifyRecommendationFeatures recommendationFeatures) {

    log.info(
        "Prepared seed tracks: {}, seed artists: {}, seed genres: {} and recommendation features: {}",
        tracksSeed,
        artistsSeed,
        genresSeed,
        recommendationFeatures);

    GetSpotifyUserItemsRequest request =
        recommendationMapper.toSpotifyRequest(
            tracksSeed, artistsSeed, genresSeed, recommendationFeatures, 50, 0);

    SpotifyItemsResponse response = spotifyClient.getRecommendations(request);

    if (response == null) {
      throw new RuntimeException("Spotify tracks recommendation response is null");
    }

    List<Map<String, Object>> spotifySeed = ((UserRecommendedItemsResponse) response).getSeeds();

    if (Objects.isNull(spotifySeed)) {
      log.warn("Spotify tracks recommendation seed is null");
      throw new RuntimeException();
    }

    log.info(
        "Spotify tracks recommendation seed received {}",
        ((UserRecommendedItemsResponse) response).getSeeds());

    List<SpotifyItem> spotifyItems = response.getItems();

    if (Objects.isNull(spotifyItems)) {
      log.error("Spotify tracks recommendation is null");
      throw new RuntimeException();
    }

    if (spotifyItems.isEmpty()) {
      log.warn("Spotify tracks recommendation is empty");
      return Collections.emptyList();
    }

    List<SpotifyTrack> tracksRecommendation =
        spotifyItems.stream()
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .collect(Collectors.toList());

    log.info("Spotify tracks recommendation received: {}", tracksRecommendation);
    return tracksRecommendation;
  }
}

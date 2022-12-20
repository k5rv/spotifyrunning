package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetRecommendationItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.client.dto.responses.UserRecommendedItemsResponse;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.recommendation.RecommendationMapper;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RecommendationService implements SpotifyRecommendationService {
  private final SpotifyClient spotifyClient;
  private final TrackMapper trackMapper;
  private final RecommendationMapper recommendationMapper;

  @Override
  public List<SpotifyTrack> getTracksRecommendation(
      @Size(min = 1, max = 5) List<SpotifyTrack> seedTracks,
      @Size(min = 1, max = 5) List<SpotifyArtist> seedArtists,
      @Size(min = 1, max = 5) List<String> seedGenres,
      @NotNull SpotifyRecommendationFeatures recommendationFeatures) {

    log.info(
        "Prepared seed tracks: {}, seed artists: {}, seed genres: {} and recommendation features: {}",
        seedTracks,
        seedArtists,
        seedGenres,
        recommendationFeatures);

    GetSpotifyUserItemsRequest request =
        GetRecommendationItemsRequest.builder()
            .seedTracks(seedTracks.stream().map(SpotifyTrack::getId).toList())
            .seedArtists(seedArtists.stream().map(SpotifyArtist::getId).toList())
            .seedGenres(seedGenres)
            .spotifyRecommendationFeatures(recommendationFeatures)
            .build();

    SpotifyItemsResponse response = spotifyClient.getRecommendations(request);

    List<Map<String, Object>> spotifySeed = ((UserRecommendedItemsResponse) response).getSeeds();

    if (Objects.isNull(spotifySeed)) {
      log.warn("Spotify seed tracks recommendation is null");
      throw new RuntimeException();
    }

    log.info(
        "Spotify seed tracks recommendation received: {}",
        ((UserRecommendedItemsResponse) response).getSeeds());

    List<SpotifyTrack> tracksRecommendation =
        response.getItems().stream()
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .map(SpotifyTrack.class::cast)
            .toList();

    log.info("Spotify tracks recommendation received: {}", tracksRecommendation);
    return tracksRecommendation;
  }
}

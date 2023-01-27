package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrunning.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrunning.config.requests.SpotifyGetRecommendationsRequestConfig;
import com.ksaraev.spotifyrunning.exception.InvalidRecommendationsSeedException;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyMapper;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RecommendationsService implements SpotifyRecommendationsService {
  private final SpotifyClient spotifyClient;

  private final TrackMapper trackMapper;

  private final SpotifyMapper spotifyMapper;

  private final SpotifyGetRecommendationsRequestConfig requestConfig;

  @Override
  public List<SpotifyTrack> getTracks(
      List<SpotifyTrack> seedTracks,
      List<SpotifyArtist> seedArtists,
      List<String> seedGenres,
      SpotifyTrackFeatures trackFeatures) {

    int seedSize =
        Stream.of(seedTracks, seedArtists, seedGenres)
            .filter(Objects::nonNull)
            .mapToInt(List::size)
            .sum();

    if (seedSize <= 0 || seedSize > 5) {
      throw new InvalidRecommendationsSeedException(
          "Expected seed size must be in [1...5] range, actual is " + seedSize);
    }

    GetRecommendationsRequest request =
        GetRecommendationsRequest.builder()
            .seedTracks(spotifyMapper.toSeed(seedTracks))
            .seedArtists(spotifyMapper.toSeed(seedArtists))
            .seedGenres(seedGenres)
            .trackFeatures(trackMapper.toGetRecommendationsRequestTrackFeatures(trackFeatures))
            .limit(requestConfig.getLimit())
            .build();

    GetRecommendationsResponse response = spotifyClient.getRecommendations(request);
    List<SpotifyTrack> tracks = trackMapper.toModel(response);
    if (tracks.isEmpty()) return List.of();
    return tracks;
  }
}

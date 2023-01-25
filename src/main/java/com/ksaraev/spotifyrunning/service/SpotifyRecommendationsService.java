package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationsFeatures;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyRecommendationsService {

  List<SpotifyTrack> getTracks(
      @NotNull List<SpotifyTrack> seedTracks,
      @NotNull List<SpotifyArtist> seedArtists,
      @NotNull List<String> seedGenres,
      SpotifyRecommendationsFeatures recommendationFeatures);
}

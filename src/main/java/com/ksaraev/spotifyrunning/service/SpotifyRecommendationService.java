package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public interface SpotifyRecommendationService {

  List<SpotifyTrack> getTracksRecommendation(
      @Size(min = 1, max = 5) List<SpotifyTrack> tracksSeed,
      @Size(min = 1, max = 5) List<SpotifyArtist> artistsSeed,
      @Size(min = 1, max = 5) List<String> genresSeed,
      @NotNull SpotifyRecommendationFeatures recommendationFeatures);
}

package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;

import java.util.List;

public interface SpotifyRecommendationsService {

  List<SpotifyTrack> getTracks(
      List<SpotifyTrack> seedTracks,
      List<SpotifyArtist> seedArtists,
      List<String> seedGenres,
      SpotifyRecommendationFeatures recommendationFeatures);
}

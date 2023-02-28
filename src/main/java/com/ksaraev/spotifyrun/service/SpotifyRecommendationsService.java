package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface SpotifyRecommendationsService {

  List<SpotifyTrack> getRecommendations(
      @NotNull @Size(min = 1, max = 5) @Valid List<@NotNull SpotifyTrack> seedTracks,
      SpotifyTrackFeatures trackFeatures);
}

package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackItemFeatures;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface SpotifyRecommendationItemsService {

  List<SpotifyTrackItem> getRecommendations(
      @NotNull @Size(min = 1, max = 5) @Valid List<@NotNull SpotifyTrackItem> seedTracks);

  List<SpotifyTrackItem> getRecommendations(
      @NotNull @Size(min = 1, max = 5) @Valid List<@NotNull SpotifyTrackItem> seedTracks,
      @NotNull SpotifyTrackItemFeatures trackFeatures);
}

package com.ksaraev.spotifyrunning.config;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.model.playlist.SpotifyPlaylistDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunningPlaylistConfig implements SpotifyRunningPlaylistConfig {

  private SpotifyPlaylistDetails spotifyPlaylistDetails;

  private SpotifyRecommendationFeatures spotifyRecommendationFeatures;
}

package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface SpotifyRecommendationsService {

  List<@Valid SpotifyTrack> getTracks(
      List<@Valid SpotifyTrack> seedTracks,
      List<@Valid SpotifyArtist> seedArtists,
      List<@NotEmpty String> seedGenres,
      SpotifyTrackFeatures trackFeatures);
}

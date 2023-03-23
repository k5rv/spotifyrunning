package com.ksaraev.spotifyrun.model.spotify.trackfeatures;

import com.ksaraev.spotifyrun.client.dto.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyTrackFeaturesMapper extends SpotifyMapper {
  GetRecommendationsRequest.TrackFeatures mapToRequestFeatures(
      SpotifyTrackItemFeatures trackFeatures);
}

package com.suddenrun.spotify.model.trackfeatures;

import com.suddenrun.client.dto.GetRecommendationsRequest;
import com.suddenrun.spotify.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyTrackFeaturesMapper extends SpotifyMapper {
  GetRecommendationsRequest.TrackFeatures mapToRequestFeatures(
      SpotifyTrackItemFeatures trackFeatures);
}

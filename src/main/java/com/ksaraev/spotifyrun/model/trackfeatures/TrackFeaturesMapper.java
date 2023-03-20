package com.ksaraev.spotifyrun.model.trackfeatures;

import com.ksaraev.spotifyrun.client.api.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrackFeaturesMapper extends SpotifyMapper {
  GetRecommendationsRequest.TrackFeatures mapToRequestFeatures(SpotifyTrackFeatures trackFeatures);
}

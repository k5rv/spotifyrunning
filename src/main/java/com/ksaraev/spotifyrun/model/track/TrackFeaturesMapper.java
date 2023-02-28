package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrackFeaturesMapper extends SpotifyMapper {
  GetRecommendationsRequest.TrackFeatures mapToRequestFeatures(SpotifyTrackFeatures trackFeatures);
}

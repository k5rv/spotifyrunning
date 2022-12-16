package com.ksaraev.spotifyrunning.model.recommendation;

import com.ksaraev.spotifyrunning.client.dto.recommendation.SpotifyRecommendationFeatures;
import com.ksaraev.spotifyrunning.client.dto.requests.GetRecommendationItemsRequest;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.spotifyentity.SpotifyEntity;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AbstractRecommendationMapper {

  @Mapping(source = "spotifyTracks", target = "seedTracks")
  @Mapping(source = "spotifyArtists", target = "seedArtists")
  @Mapping(source = "genres", target = "seedGenres")
  @Mapping(source = "limit", target = "limit")
  @Mapping(source = "offset", target = "offset")
  public abstract GetRecommendationItemsRequest toSpotifyRequest(
      List<SpotifyTrack> spotifyTracks,
      List<SpotifyArtist> spotifyArtists,
      List<String> genres,
      SpotifyRecommendationFeatures recommendationFeatures,
      Integer limit,
      Integer offset);

  private List<String> mapSpotifyEntitiesToSeedIds(List<SpotifyEntity> spotifyEntities) {
    if (spotifyEntities == null) {
      return Collections.emptyList();
    }
    return spotifyEntities.stream().map(SpotifyEntity::getId).collect(Collectors.toList());
  }

  public List<String> mapSpotifyTracksToSeedIds(List<SpotifyTrack> spotifyTrackList) {
    return mapSpotifyEntitiesToSeedIds(
        spotifyTrackList.stream().map(t -> ((SpotifyEntity) t)).collect(Collectors.toList()));
  }

  public List<String> mapSpotifyArtistsToSeedIds(List<SpotifyArtist> spotifyArtistList) {
    return mapSpotifyEntitiesToSeedIds(
        spotifyArtistList.stream().map(t -> ((SpotifyEntity) t)).collect(Collectors.toList()));
  }
}

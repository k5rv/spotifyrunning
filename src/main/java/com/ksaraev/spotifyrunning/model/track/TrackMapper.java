package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemMusic;
import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemTrack;
import com.ksaraev.spotifyrunning.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrunning.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrunning.model.artist.ArtistMapper;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrackFeatures;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Objects;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ArtistMapper.class})
public interface TrackMapper {

  @Mapping(target = "artists", source = "artistItems")
  Track toModel(SpotifyTrackItem spotifyTrackItem);

  default List<SpotifyTrack> toModel(SpotifyPlaylistItemMusic playlistItemMusic) {
    return playlistItemMusic.playlistItemTracks().stream()
        .filter(Objects::nonNull)
        .map(SpotifyPlaylistItemTrack::trackItem)
        .map(this::toModel)
        .map(SpotifyTrack.class::cast)
        .toList();
  }

  GetRecommendationsRequest.TrackFeatures toGetRecommendationsRequestTrackFeatures(
      SpotifyTrackFeatures trackFeatures);
}

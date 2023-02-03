package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemMusic;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemTrack;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.client.requests.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.responses.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.model.artist.ArtistMapper;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrackFeatures;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ArtistMapper.class})
public interface TrackMapper {

  @Mapping(target = "artists", source = "artistItems")
  Track toModel(SpotifyTrackItem spotifyTrackItem);

  GetRecommendationsRequest.TrackFeatures toGetRecommendationsRequestTrackFeatures(
      SpotifyTrackFeatures trackFeatures);

  default List<SpotifyTrack> toModel(List<SpotifyTrackItem> trackItems) {
    if (CollectionUtils.isEmpty(trackItems)) return List.of();
    return trackItems.stream()
        .filter(Objects::nonNull)
        .map(this::toModel)
        .map(SpotifyTrack.class::cast)
        .toList();
  }

  default List<SpotifyTrack> toModel(GetRecommendationsResponse response) {
    if (response == null) return List.of();
    if (CollectionUtils.isEmpty(response.trackItems())) return List.of();
    return toModel(response.trackItems());
  }

  default List<SpotifyTrack> toModel(SpotifyPlaylistItemMusic playlistItemMusic) {
    if (playlistItemMusic == null) return List.of();
    if (CollectionUtils.isEmpty(playlistItemMusic.playlistItemTracks())) return List.of();
    List<SpotifyTrackItem> trackItems =
        playlistItemMusic.playlistItemTracks().stream()
            .filter(Objects::nonNull)
            .map(SpotifyPlaylistItemTrack::trackItem)
            .toList();
    return toModel(trackItems);
  }
}

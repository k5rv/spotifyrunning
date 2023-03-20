package com.ksaraev.spotifyrun.model.track;

import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistItemTrack;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.artist.ArtistMapper;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ArtistMapper.class})
public interface TrackMapper extends SpotifyMapper {

  @Mapping(target = "artists", source = "artistItems")
  Track mapToTrack(SpotifyTrackItem spotifyTrackItem);

  default List<SpotifyTrack> mapItemsToTracks(List<SpotifyTrackItem> trackItems) {
    if (trackItems == null) return List.of();
    return trackItems.stream()
        .filter(Objects::nonNull)
        .map(this::mapToTrack)
        .map(SpotifyTrack.class::cast)
        .toList();
  }

  default List<SpotifyTrack> mapPlaylistItemsToTracks(
      List<SpotifyPlaylistItemTrack> playlistItemTracks) {
    if (playlistItemTracks == null) return List.of();
    return playlistItemTracks.stream()
        .filter(Objects::nonNull)
        .map(SpotifyPlaylistItemTrack::trackItem)
        .map(this::mapToTrack)
        .map(SpotifyTrack.class::cast)
        .toList();
  }
}

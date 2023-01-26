package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.client.dto.items.playlist.content.SpotifyPlaylistItemContent;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.content.SpotifyAddableTrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.track.SpotifyTrackItem;
import com.ksaraev.spotifyrunning.model.artist.ArtistMapper;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyTrack;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Objects;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ArtistMapper.class})
public interface TrackMapper {

  Track toModel(SpotifyTrackItem spotifyTrackItem);

  default List<SpotifyTrack> toModel(
      SpotifyPlaylistItemContent spotifyPlaylistItemContent) {
    return spotifyPlaylistItemContent.getItems().stream()
        .filter(Objects::nonNull)
        .map(SpotifyAddableTrackItem::getSpotifyTrackItem)
        .map(this::toModel)
        .map(SpotifyTrack.class::cast)
        .toList();
  }
}

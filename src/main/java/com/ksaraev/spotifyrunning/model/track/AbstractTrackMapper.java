package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.client.dto.items.playlist.AddableTrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistContent;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.model.artist.ArtistMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ArtistMapper.class})
public abstract class AbstractTrackMapper implements TrackMapper {

  public abstract Track toTrack(TrackItem trackItem);

  public List<SpotifyTrack> playlistTracksToSpotifyTracks(
      PlaylistContent<AddableTrackItem> playlistContent) {

    if (Objects.isNull(playlistContent)) {
      throw new IllegalArgumentException("value is null");
    }

    return playlistContent.getItems().stream()
        .map(AddableTrackItem::getSpotifyItem)
        .filter(Objects::nonNull)
        .map(TrackItem.class::cast)
        .map(this::toTrack)
        .collect(Collectors.toList());
  }
}

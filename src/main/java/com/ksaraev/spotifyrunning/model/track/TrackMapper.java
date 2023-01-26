package com.ksaraev.spotifyrunning.model.track;

import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemMusic;
import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemTrack;
import com.ksaraev.spotifyrunning.client.items.SpotifyTrackItem;
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

  default List<SpotifyTrack> toModel(SpotifyPlaylistItemMusic spotifyPlaylistItemTracks) {
    return spotifyPlaylistItemTracks.spotifyPlaylistItemTracks().stream()
        .filter(Objects::nonNull)
        .map(SpotifyPlaylistItemTrack::spotifyTrackItem)
        .map(this::toModel)
        .map(SpotifyTrack.class::cast)
        .toList();
  }
}

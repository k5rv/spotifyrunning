package com.ksaraev.spotifyrunning.model.playlist.details;

import com.ksaraev.spotifyrunning.client.dto.items.playlist.details.SpotifyPlaylistDetailsDTO;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlaylistDetailsMapper {
  SpotifyPlaylistDetailsDTO toDto(SpotifyPlaylistDetails spotifyPlaylistDetails);
}

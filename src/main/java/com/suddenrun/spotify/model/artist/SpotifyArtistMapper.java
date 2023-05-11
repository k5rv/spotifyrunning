package com.suddenrun.spotify.model.artist;

import com.suddenrun.client.dto.SpotifyArtistDto;
import com.suddenrun.spotify.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyArtistMapper extends SpotifyMapper {
  SpotifyArtist mapToArtist(SpotifyArtistDto artistItem);
}

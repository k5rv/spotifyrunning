package com.ksaraev.spotifyrun.spotify.model.artist;

import com.ksaraev.spotifyrun.client.dto.SpotifyArtistDto;
import com.ksaraev.spotifyrun.spotify.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyArtistMapper extends SpotifyMapper {
  SpotifyArtist mapToArtist(SpotifyArtistDto artistItem);
}
